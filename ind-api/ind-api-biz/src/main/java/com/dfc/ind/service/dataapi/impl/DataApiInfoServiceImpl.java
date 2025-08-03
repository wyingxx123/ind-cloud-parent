package com.dfc.ind.service.dataapi.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dfc.ind.common.core.constant.Constants;
import com.dfc.ind.common.core.exception.CustomException;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.utils.secret.AESUtil;
import com.dfc.ind.common.core.utils.sign.CallApiJwtUtil;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.redis.service.RedisService;
import com.dfc.ind.entity.dataapi.DataApiEngineInfoEntity;
import com.dfc.ind.entity.dataapi.DataApiInfoEntity;
import com.dfc.ind.entity.dataapi.DataApiParaInfoEntity;
import com.dfc.ind.entity.dataapi.param.LoadApiDataToRedisParam;
import com.dfc.ind.entity.dataapi.vo.*;
import com.dfc.ind.mapper.dataapi.DataApiInfoMapper;
import com.dfc.ind.service.dataapi.IDataApiEngineInfoService;
import com.dfc.ind.service.dataapi.IDataApiInfoService;
import com.dfc.ind.service.dataapi.IDataApiParaInfoService;
import com.dfc.ind.service.dataapi.IDbAdapterService;
import com.dfc.ind.uitls.*;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.sequoiadb.base.ConfigOptions;
import com.sequoiadb.datasource.ConnectStrategy;
import com.sequoiadb.datasource.DatasourceOptions;
import com.sequoiadb.datasource.SequoiadbDatasource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author huff
 * @since 2022-09-08
 */
@Service
@Slf4j
public class DataApiInfoServiceImpl extends MppServiceImpl<DataApiInfoMapper, DataApiInfoEntity> implements IDataApiInfoService {
    @Autowired
    private IDataApiEngineInfoService dataApiEngineInfoService;

    @Autowired
    private IDataApiParaInfoService dataApiParaInfoService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IDbAdapterService dbAdapterService;
    private static final String[] tableNameArr = {"data_api_info", "data_api_para_info", "data_api_source_para_info", "data_api_engine_info", "data_api_app_info", "data_api_source_info"};

    @Value("${apiAuthorizeFailedRetryCount:5}")
    private Integer failedRetryCount;

    @Value("${dataSourceInitSize:10}")
    private Integer dataSourceInitSize;

    @Value("${env.type:DEV}")
    private String env_type;





    /**
     * api授权失败初始冻结时间(分钟)
     */
    @Value("${apiAuthorizeFreezeTime:5}")
    private Integer freezeTime;
    //7776000L
    private Long cacheExpireTime = null;

    @Override
    @Transactional
    public JsonResults saveAllToDataApiByAppId(String applicationCode) {

        List<ApiInfoCatchDTO> list = baseMapper.getApiDataByAppId(applicationCode);
        return saveToDataApi(null, list, true);
    }

    @Override
    public JsonResults clearRedis() {
        redisService.redisTemplate.getConnectionFactory().getConnection().flushDb();
        return JsonResults.success("清空缓存成功");
    }

    @Override
    public JsonResults pageAll(Page startPage, DataApiInfoEntity entity) {
        LambdaQueryWrapper<DataApiInfoEntity> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper
                .like(StringUtils.isNotEmpty(entity.getApiName()), DataApiInfoEntity::getApiName, entity.getApiName())
                .like(StringUtils.isNotEmpty(entity.getApiId()), DataApiInfoEntity::getApiId, entity.getApiId())
                .like(StringUtils.isNotEmpty(entity.getApiPath()), DataApiInfoEntity::getApiPath, entity.getApiPath())

                .eq(StringUtils.isNotEmpty(entity.getAppId()), DataApiInfoEntity::getAppId, entity.getAppId())
                .eq(DataApiInfoEntity::getDelFlg, "0");
        Page page = this.page(startPage, queryWrapper);
        return JsonResults.success(page);
    }





    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonResults saveToDataApi(HttpServletRequest request, List<ApiInfoCatchDTO> list, Boolean isAll) {
        ArrayList<DataApiInfoEntity> apiInfoEntities = new ArrayList<>();
        ArrayList<DataApiEngineInfoEntity> apiEngineInfoEntities = new ArrayList<>();
        ArrayList<DataApiParaInfoEntity> apiParaInfoEntities = new ArrayList<>();
        if (isAll) {
            UpdateWrapper<DataApiInfoEntity> infoEntityUpdateWrapper = new UpdateWrapper<>();
            infoEntityUpdateWrapper.lambda().eq(DataApiInfoEntity::getAppId, list.get(0).getAppId());
            this.remove(infoEntityUpdateWrapper);
            UpdateWrapper<DataApiParaInfoEntity> apiEngineInfoEntityUpdateWrapper = new UpdateWrapper<>();
            apiEngineInfoEntityUpdateWrapper.lambda().eq(DataApiParaInfoEntity::getAppId, list.get(0).getAppId());
            dataApiParaInfoService.remove(apiEngineInfoEntityUpdateWrapper);

            UpdateWrapper<DataApiEngineInfoEntity> entityUpdateWrapper = new UpdateWrapper<>();
            entityUpdateWrapper.lambda().likeRight(DataApiEngineInfoEntity::getEngineNo, ":" + list.get(0).getAppId());
            dataApiEngineInfoService.remove(entityUpdateWrapper);
        }
        for (ApiInfoCatchDTO apiInfoCatchDTO : list) {
            String apiCode = apiInfoCatchDTO.getApiCode();
            DataApiInfoEntity dataApiInfoEntity = new DataApiInfoEntity();
            dataApiInfoEntity.setServiceId(apiCode + "_" + apiInfoCatchDTO.getVersion());
            dataApiInfoEntity.setAppId(apiInfoCatchDTO.getAppId());
            dataApiInfoEntity.setApiId(apiCode);
            dataApiInfoEntity.setApiCallType(apiInfoCatchDTO.getShareType());
            dataApiInfoEntity.setApiCallCountLimit(apiInfoCatchDTO.getShareCountLimit());
            dataApiInfoEntity.setApiVersion(apiInfoCatchDTO.getVersion().toString());
            dataApiInfoEntity.setApiPath(apiInfoCatchDTO.getVersion() + apiInfoCatchDTO.getPath());
            //默认单引擎
            dataApiInfoEntity.setServiceType(StringUtils.isNotEmpty(apiInfoCatchDTO.getType()) ? apiInfoCatchDTO.getType() : Constants.SERVICE_TYPE_ENGINE);
            dataApiInfoEntity.setServiceStatus("1");
            dataApiInfoEntity.setServiceName(apiInfoCatchDTO.getName());
            HashMap<Object, Object> resource = new HashMap<>();
            resource.put("engineNo", apiCode + apiInfoCatchDTO.getVersion() + apiInfoCatchDTO.getPath() + ":" + dataApiInfoEntity.getAppId());
            dataApiInfoEntity.setServiceResource(JSON.toJSONString(resource));
            dataApiInfoEntity.setAuthStartDate(apiInfoCatchDTO.getPubEffectiveStartDate());
            dataApiInfoEntity.setAuthEndDate(apiInfoCatchDTO.getPubEffectiveEndDate());
            apiInfoEntities.add(dataApiInfoEntity);
            if (StringUtils.isNotEmpty(apiInfoCatchDTO.getRequestParam())) {
                DataApiParaInfoEntity apiParaInfoEntity = new DataApiParaInfoEntity();
                apiParaInfoEntity.setServiceId(dataApiInfoEntity.getServiceId());
                apiParaInfoEntity.setAppId(dataApiInfoEntity.getAppId());
                apiParaInfoEntity.setParaGroupNo("MetaParam");
                apiParaInfoEntity.setParaId("3");
                apiParaInfoEntity.setParaStatus("1");
                apiParaInfoEntity.setParaType("JSON");
                apiParaInfoEntity.setParaName("RequestParam");
                apiParaInfoEntity.setParaResource(apiInfoCatchDTO.getRequestParam());
                apiParaInfoEntity.setResponseParam(apiInfoCatchDTO.getResponseParam());
                apiParaInfoEntities.add(apiParaInfoEntity);
            }
            if (StringUtils.isNotEmpty(apiInfoCatchDTO.getResponseParam())) {
                DataApiParaInfoEntity apiParaInfo = new DataApiParaInfoEntity();
                apiParaInfo.setServiceId(dataApiInfoEntity.getServiceId());
                apiParaInfo.setAppId(dataApiInfoEntity.getAppId());
                apiParaInfo.setParaGroupNo("MetaParam");
                apiParaInfo.setParaId("4");
                apiParaInfo.setParaStatus("1");
                apiParaInfo.setParaType("JSON");
                apiParaInfo.setParaName("ResponseParam");
                apiParaInfo.setParaResource(apiInfoCatchDTO.getResponseParam());
                apiParaInfoEntities.add(apiParaInfo);
            }
            DataApiEngineInfoEntity dataApiEngineInfoEntity = new DataApiEngineInfoEntity();
            dataApiEngineInfoEntity.setEngineNo(apiCode + apiInfoCatchDTO.getVersion() + apiInfoCatchDTO.getPath());
            if (ApiInfoConstant.SQL_TYPE_QUERY.equals(apiInfoCatchDTO.getSqlType())) {
                dataApiEngineInfoEntity.setEngineType(ApiInfoConstant.SQL_QUERY);
            } else {
                dataApiEngineInfoEntity.setEngineType(ApiInfoConstant.SQL_EXEC);
            }
            dataApiEngineInfoEntity.setAppId(dataApiInfoEntity.getAppId());
            dataApiEngineInfoEntity.setEngineStatus("1");
            dataApiEngineInfoEntity.setEngineName(apiInfoCatchDTO.getName());
            dataApiEngineInfoEntity.setEngineSource(null);
            SqlExecuteDTO executeDTO = JSON.parseObject(apiInfoCatchDTO.getSqlJob(), SqlExecuteDTO.class);
            dataApiEngineInfoEntity.setEngineResource(executeDTO.getSql());
            apiEngineInfoEntities.add(dataApiEngineInfoEntity);
        }
        dataApiEngineInfoService.saveOrUpdateBatchByMultiId(apiEngineInfoEntities);
        dataApiParaInfoService.saveOrUpdateBatchByMultiId(apiParaInfoEntities);
        this.saveOrUpdateBatchByMultiId(apiInfoEntities);
        return JsonResults.success("发布成功");
    }

    @Synchronized
    @Override
    public JsonResults loadRedis(LoadApiDataToRedisParam param) {
        if (param.getAppId() == null) {
            return JsonResults.error("appId不能为空");
        }
        if (param.getSchema() == null) {
            return JsonResults.error("schema不能为空");
        }
        String appId = param.getAppId();
        String tableName = param.getTableName();

        String moduleName = this.getClass().getName() + ".loadRedis";
        //redis有效期检查，有效天数计算
        if (param.getExpireDays() != null) {
            cacheExpireTime = param.getExpireDays() * 24 * 3600;
        }
        if (StringUtils.isNotEmpty(tableName)) {
            this.sysDb2Redis(tableName, moduleName, param);
        } else {
            //未指定类型则初始化应用所有缓存数据
            for (String name : tableNameArr) {
                param.setTableName(name);
                this.sysDb2Redis(name, moduleName, param);
            }
        }
        //加载数据源
        innitResourceByAppId(param.getAppId());
        return JsonResults.success("刷新缓存成功:appId=" + appId);
    }

    private void sysDb2Redis(String tableName, String moduleName, LoadApiDataToRedisParam param) {
        StringBuilder stringBuilder = new StringBuilder();
        String clearDataInfoKey = "";
        switch (tableName) {
            case "data_api_info":
                clearDataInfoKey = tableName + ":*:" + param.getAppId() + ":";
                stringBuilder.append("and service_status='1' and app_id='").append(param.getAppId()).append("'");
                param.setAndSql(stringBuilder.toString());
                loadToRedis(moduleName, param, clearDataInfoKey);
                break;
            case "data_api_para_info":
                clearDataInfoKey = tableName + ":*:" + param.getAppId() + ":";
                stringBuilder = new StringBuilder();
                stringBuilder.append(" and para_status='1' and app_id='").append(param.getAppId()).append("'");
                param.setAndSql(stringBuilder.toString());

                loadToRedis(moduleName, param, clearDataInfoKey);
                break;
            case "data_api_engine_info":
                clearDataInfoKey = tableName + ":*:" + param.getAppId() + ":";
                stringBuilder = new StringBuilder();
                stringBuilder.append(" and engine_status='1'");
                param.setAndSql(stringBuilder.toString());

                loadToRedis(moduleName, param, clearDataInfoKey);
                break;
/*            case "DATA_API_ENGINE_PARA_INFO":
                clearDataInfoKey = "DATA_API_ENGINE_PARA_INFO:*:" + appId + ":";
                stringBuilder = new StringBuilder();
                stringBuilder.append("and EngineParaStatus='1'");
                loadToRedis(tableName, moduleName, param, clearDataInfoKey, stringBuilder);
                break;
            case "DATA_API_MODULE_PARA_INFO":
                clearDataInfoKey = "DATA_API_MODULE_PARA_INFO:*:" + appId + ":";
                stringBuilder = new StringBuilder();
                stringBuilder.append("and ParaStatus='1'");
                loadToRedis(tableName, moduleName, param, clearDataInfoKey, stringBuilder);
                break;*/
            case "data_api_app_info":
                clearDataInfoKey = "data_api_app_info:*";
                stringBuilder = new StringBuilder();
                stringBuilder.append(" and app_status='1' and del_flg='0' ");
                param.setAndSql(stringBuilder.toString());

                loadToRedis(moduleName, param, clearDataInfoKey);
                break;
            case "data_api_source_info":
                clearDataInfoKey = tableName + ":*";
                stringBuilder = new StringBuilder();
                stringBuilder.append(" and del_flg='0' and source_status='0'");
                param.setAndSql(stringBuilder.toString());
                loadToRedis(moduleName, param, clearDataInfoKey);
                break;
            case "data_api_source_para_info":
                clearDataInfoKey = tableName + ":*";
                stringBuilder = new StringBuilder();
                stringBuilder.append(" and del_flg='0' and para_status='0'");
                param.setAndSql(stringBuilder.toString());
                loadToRedis(moduleName, param, clearDataInfoKey);
                break;
            default:
                throw new CustomException("刷新缓存失败:不存在的tableName" + tableName);
        }

    }


    private void loadToRedis(String moduleName, LoadApiDataToRedisParam param, String clearDataInfoKey) {
        //清缓存
        clearCatchByKey(clearDataInfoKey);
        List<Map<String, Object>> dataApiInfoList = baseMapper.getData(param);
        //读取元数据
        List<MetaDataVo> metaDataVoList;

        metaDataVoList = baseMapper.getMetaDataByTableName(param);

        if (!CollectionUtils.isEmpty(dataApiInfoList)) {
            for (Map<String, Object> map : dataApiInfoList) {
                String loadKey = param.getTableName();
                Map<String, Object> loadData = new HashMap<>();
                for (MetaDataVo metaDataVo : metaDataVoList) {
                    String isPk = metaDataVo.getIsPk();
                    String name = metaDataVo.getName();
                    if (map.get(name) != null) {
                        if (map.get(name) instanceof Timestamp) {
                            map.put(name, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, (Date) map.get(name)));
                        }
                        String redisStr = map.get(name).toString().replace(" ", "\\x00");
                        loadData.put(name, redisStr);
                    }
                    if ("1".equals(isPk)) {
                        loadKey = loadKey + ":" + map.get(name);
                    }
                }
                if (!CollectionUtils.isEmpty(loadData)) {
                    if ("data_api_info".equals(param.getTableName())) {
                        String pathKey = param.getTableName() + ":" + loadData.get("api_path").toString() + ":" + loadData.get("app_id").toString();
                        Map<String, Object> pathData = new HashMap<>();
                        pathData.put("pathValue", loadData.get("api_id").toString());
                        redisService.setCacheMap(pathKey, pathData);
                        if (cacheExpireTime != null) {
                            redisService.expire(pathKey, cacheExpireTime);
                        }
                    }
                    redisService.setCacheMap(loadKey, loadData);
                    if (cacheExpireTime != null) {
                        redisService.expire(loadKey, cacheExpireTime);
                        }


                } else {
                    log.warn("{} failed ... load data none ... dataPara=[{}]", moduleName, param);
                }
            }
        }
    }

    private void clearCatchByKey(String clearDataInfoKey) {
        Collection<String> cacheHashByKeys = redisService.keys(clearDataInfoKey + "*");
        for (String key : cacheHashByKeys) {
            redisService.deleteObject(key);
        }

    }

    @Override
    public Object apiServer(String apiPath, HttpServletRequest request) throws Exception {
        long start = System.currentTimeMillis();
        try {

            String callParam = request2Map(request);
            String moduleName = this.getClass().getName() + ".apiServer";
            Map<String, Object> apiBusiData;
            String applicationCode = CallApiJwtUtil.getUserId();
            log.info("{} Begin to deal apiPath={}", moduleName, apiPath);
            if (StringUtils.isNotEmpty(apiPath) && apiPath.contains("/")) {
                //读取API缓存信息
                String pathKey = "data_api_info:" + apiPath + ":" + applicationCode;
                String apiId = redisService.getCacheMapValue(pathKey, "pathValue");
                if (StringUtils.isEmpty(apiId)) {
                    log.error("api:{}不存在", pathKey);
                    throw new CustomException("api不存在" + pathKey);
                }
                apiId = apiId.replace("\\x00", " ");
                long l = System.currentTimeMillis();
                apiBusiData = this.getApiCacheInfo(applicationCode, apiId, apiPath.split("/")[0]);
                long l1 = System.currentTimeMillis();
              long d=  l1-l;
                System.out.println("getApiCacheInfo耗时 = " + d);
                //请求参数必填判断
                if (apiBusiData.containsKey(ApiInfoConstant.API_PARA)) {
                    Map<String, Object> api_para = (Map<String, Object>) apiBusiData.get(ApiInfoConstant.API_PARA);
                    if (api_para.get("para_resource") != null) {
                        String paraResource = api_para.get("para_resource").toString();
                        callParam = paramRequired(callParam, paraResource);
                    }
                }
                apiBusiData.put(ApiInfoConstant.CALL_PARAM, callParam);
                //调用API配置的引擎处理

                return this.callEngine(apiBusiData);

            } else {
                throw new CustomException("api路径错误");
            }

        } catch (Exception e) {
            log.error("api:"+apiPath+"调用失败", e);
            throw e;
        } finally {
            long end = System.currentTimeMillis();
            String user_id = request.getHeader("USER_ID");

                log.info("USER_ID:{},api:{}调用结束总共耗时:{}毫秒",user_id,apiPath,end-start);

        }

    }

    private static String paramRequired(String callParam, String requestParamStr) throws CustomException {
        if (!org.springframework.util.StringUtils.isEmpty(requestParamStr)) {
            List<Map> requestParam = JSON.parseArray(requestParamStr, Map.class);
            if (!CollectionUtils.isEmpty(requestParam)) {
                for (Map map : requestParam) {
                    String id = map.get(ApiInfoConstant.ID).toString();
                    if (callParam.startsWith("[")) {
                        List<Map> mapList = JSON.parseArray(callParam, Map.class);
                        for (Map callMap : mapList) {
                            if (map.containsKey(ApiInfoConstant.REQUIRED) && Integer.parseInt(map.get(ApiInfoConstant.REQUIRED).toString()) == 1) {
                                if (!callMap.containsKey(id) || callMap.get(id) == null || org.springframework.util.StringUtils.isEmpty(callMap.get(id).toString())) {
                                    throw new CustomException("必填参数未填写:" + id);
                                }
                            }
                            //请求为null或空串的字符串设置为null
                            if (callMap.get(id) != null) {
                                if ("".equals(callMap.get(id).toString()) || ApiInfoConstant.NULL.equalsIgnoreCase(callMap.get(id).toString())) {
                                    callMap.put(id, null);
                                }
                            }
                        }
                        callParam = JSON.toJSONString(mapList);
                    } else {
                        Map callMap = JSON.parseObject(callParam, Map.class);
                        if (map.containsKey(ApiInfoConstant.REQUIRED) && Integer.parseInt(map.get(ApiInfoConstant.REQUIRED).toString()) == 1) {
                            if (!callMap.containsKey(id) || callMap.get(id) == null || org.springframework.util.StringUtils.isEmpty(callMap.get(id).toString())) {
                                throw new CustomException("必填参数未填写:" + id);
                            }
                        }
                        //请求为null或空串的字符串设置为null
                        if (callMap.get(id) != null) {
                            if ("".equals(callMap.get(id).toString()) || ApiInfoConstant.NULL.equalsIgnoreCase(callMap.get(id).toString())) {
                                callMap.put(id, null);
                            }
                        }
                        callParam = JSON.toJSONString(callMap);
                    }


                }
            }
            return callParam;
        } else {
            return callParam;
        }
    }

    public static String request2Map(HttpServletRequest request) throws CustomException {
        String map = null;
        try {
            if (request.getParameterMap() != null && request.getParameterMap().size() != 0) {
                map = req2Map(request);
                //判断是否需要解密param
                if (map != null) {
                    map = decodeParam(JSON.parseObject(map, Map.class));
                }
            } else {
                map = getRequestPostStr(request);
            }

        } catch (Exception e) {
            log.error("参数解析失败",e);
            throw new CustomException("参数解析失败:" + e.getMessage(), e);
        }
        if (StringUtils.containsIllegalKeyword(map)) {
            throw new CustomException("参数可能存在非法sql注入,服务器拒绝访问");
        }
        return map;
    }

    public static byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {

            int readLen = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (readLen == -1) {
                break;
            }
            i += readLen;
        }
        return buffer;
    }

    /**
     * json转换成map
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestPostStr(HttpServletRequest request)
            throws IOException, CustomException {
        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        String jsonStr = new String(buffer, charEncoding);
        if (jsonStr.startsWith("[")) {
            List<LinkedHashMap> maps = JSON.parseArray(jsonStr, LinkedHashMap.class);
            return JSON.toJSONString(maps);
        } else if ("{}".equals(jsonStr)||"".equals(jsonStr)) {
            return "{}";
        } else if (jsonStr.startsWith("{")) {
            Map<String, Object> map = (Map<String, Object>) JSON.parseObject(jsonStr, Map.class);
            if (map != null) {
                return JSON.toJSONString(map);
            }
        }
        throw new CustomException("参数格式错误:" + jsonStr);


    }

    private static String decodeParam(Map<String, Object> callParam) {
        if (callParam.get(ApiInfoConstant.ENCRYPTED) != null && ApiInfoConstant.ENCRYPTED_VALUE.equals(callParam.get(ApiInfoConstant.ENCRYPTED))) {
            if (callParam.containsKey(ApiInfoConstant.PARAM)) {
                if (callParam.get(ApiInfoConstant.PARAM) != null) {
                    String param = callParam.get(ApiInfoConstant.PARAM).toString();
                    param = param.substring(1, param.length() - 1);
                    callParam.put(ApiInfoConstant.PARAM, new String(Base64Utils.decode(param.getBytes())));
                }
            }
            if (callParam.containsKey(ApiInfoConstant.SET_PARAM)) {
                if (callParam.get(ApiInfoConstant.SET_PARAM) != null) {
                    String param = callParam.get(ApiInfoConstant.SET_PARAM).toString();
                    param = param.substring(1, param.length() - 1);
                    callParam.put(ApiInfoConstant.SET_PARAM, new String(Base64Utils.decode(param.getBytes())));
                }
            }
        }
        return JSON.toJSONString(callParam);
    }

    /**
     * 将request中的Mep转换成map
     *
     * @param request
     * @return
     */
    public static String req2Map(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> map.put(key, value[0]));
        return JSON.toJSONString(map);
    }

    /**
     * 调用引擎
     * 1、引擎流类型判定
     * 2-1、单引擎处理
     * 2-1-1、引擎编号参数获取
     * 2-1-2、调用引擎执行doEngine
     * 2-1-3、引擎执行doEngine返回
     * 2-1-4、依据参数生成指定数据文件
     * 2-2、引擎流处理
     * 2-2-1、循环引擎流
     * 2-2-2、引擎流节点引擎编号参数获取
     * 2-2-3、引擎流节点参数准备
     * 2-2-4、引擎流节点参数导入API参数
     * 2-2-5、引擎流节点调用执行引擎doEngine
     * 2-2-6、引擎流节点执行引擎doEngine返回
     * 2-2-7、引擎流节点参数依据节点返回结果数据追加
     * 2-2-8、引擎流节点参数更新节点返回参数数据
     * 2-2-9、依据参数生成指定数据文件
     * 3、调用返回信息
     *
     * @param busiData
     * @return
     */
    private Object callEngine(Map<String, Object> busiData) throws Exception {
        String moduleName = this.getClass().getName() + ".callEngine";
        String engineNo = null;
        if (busiData.get("service_type") != null) {
            if ("SINGLE-ENGINE".equals(busiData.get("service_type").toString())) {
                if (busiData.get("service_resource") != null) {
                    if (busiData.get("service_resource").toString().contains("engineNo")) {
                        EngineSourceVo engineSourceVo = JSON.parseObject(busiData.get("service_resource").toString(), EngineSourceVo.class);
                        engineNo = engineSourceVo.getEngineNo();
                        return this.doEngine(engineNo, busiData);
                    } else {
                        log.error("{} failed ... parament ServiceResource.EngineNO not found ... ServiceResource [{}]", moduleName, busiData.get("service_resource"));
                        throw new CustomException("执行资源:EngineNO不存在");
                    }
                } else {
                    throw new CustomException("执行资源:ServiceResource不存在");
                }
            } else if ("FLOW-ENGINE".equals(busiData.get("service_type").toString())) {
                //多引擎执行
                if (busiData.get("service_resource") != null) {
                    if (busiData.get("service_resource").toString().contains("engineNo")) {
                        List<EngineSourceVo> engineSourceVoList = JSON.parseArray(busiData.get("service_resource").toString(), EngineSourceVo.class);

                        return this.doFlowEngine(engineSourceVoList, busiData);
                    } else {
                        log.error("{} failed ... parament ServiceResource.EngineNO not found ... ServiceResource [{}]", moduleName, busiData.get("service_resource"));
                        throw new CustomException("执行资源:EngineNO不存在");
                    }
                } else {
                    throw new CustomException("执行资源:ServiceResource不存在");
                }

            }

        } else {
            throw new CustomException("api未声明引擎类型");
        }
        return JsonResults.error("调用引擎失败");
    }

    private Object doEngine(String engineNo, Map<String, Object> busiData) throws Exception {
        String moduleName = this.getClass().getName() + ".doEngine";
        Map<String, Object> engineAttr = null;
        if (StringUtils.isNotEmpty(engineNo)) {
            long l = System.currentTimeMillis();

            engineAttr = this.getEngineCacheInfo(engineNo, busiData.get("app_id").toString());

            long l1 = System.currentTimeMillis();
            long d=  l1-l;
//            System.out.println("getEngineCacheInfo耗时 = " + d);
            if (engineAttr.containsKey("engine_info")) {
                Map<String, Object> engineInfo = (Map<String, Object>) engineAttr.get("engine_info");
                if (engineInfo.containsKey("engine_type")) {
                    if ("SQL-QUERY".equals(engineInfo.get("engine_type").toString())) {
                        return this.engineSqlQuery(engineNo, engineAttr, busiData);
                    } else if ("SQL-EXEC".equals(engineInfo.get("engine_type").toString())) {
                        return this.engineSqlExec(engineNo, engineAttr, busiData);
                    } else {
                        log.error("不支持的引擎类型:{}", engineInfo.get("engine_type").toString());
                        throw new CustomException("不支持的引擎类型:" + engineInfo.get("engine_type").toString());
                    }
                }
            }else {
                log.error("查询不到引擎信息:engineNo={}" , engineNo);
                throw new CustomException("查询不到引擎信息:engineNo=" + engineNo);
            }
        } else {
            log.error("{} failed ... parament engineNo is null ... engineNo [{}]", moduleName, engineNo);
        }
        return JsonResults.error("调用引擎失败");
    }

    private Object doFlowEngine(List<EngineSourceVo> engineList, Map<String, Object> busiData)  throws Exception{

        String moduleName = this.getClass().getName() + ".doFlowEngine";

        if (StringUtils.isNotEmpty(engineList)) {
            return this.engineMysqlExecTran(engineList, busiData);
        } else {
            log.error("{} failed ... parament engineNo is null ... engineNo [{}]", moduleName, engineList);
        }
        return JsonResults.error("调用多引擎失败");
    }
    private Object engineSqlExec(String engineNo, Map<String, Object> engineAttr, Map<String, Object> busiData) {
        String moduleName = this.getClass().getSimpleName() + ".engineMysqlQuery";
        Map<String, Object> getSql = null;
        String sqlStr = null;
        //1、读取引擎属性
        if (!CollectionUtils.isEmpty(engineAttr)) {
            getSql = this.engineGetSql(engineNo, engineAttr, busiData,true);
            if ("200".equals(getSql.get("code").toString())) {
                sqlStr = getSql.get("sql").toString();
                return dbAdapterService.execSqlExec(sqlStr, busiData);
            } else {
                log.error("{} failed ... call engineGetMysqlSql is illegal ... engineNo [{}]", moduleName, engineNo);
                throw new CustomException("读取引擎属性:engineAttr失败");

            }

        } else {
            log.error("{} failed ... parament engineAttr is null ... engineNo [{}]", moduleName, engineNo);
            throw new CustomException("读取引擎属性:engineAttr失败");
        }

    }


    private Object engineSqlQuery(String engineNo, Map<String, Object> engineAttr, Map<String, Object> busiData) throws Exception {
        String moduleName = this.getClass().getSimpleName() + ".engineMysqlQuery";
        String moduleMsg = "engine mysql query service ";
        Map<String, Object> retData = new HashMap<>();
        Map<String, Object> getSql = null;
        String sqlStr = null;
        retData.put("code", "997");
        retData.put("msg", moduleMsg + "s system error");
        //1、读取引擎属性
        if (!CollectionUtils.isEmpty(engineAttr)) {
            //sql参数替换
            getSql = this.engineGetSql(engineNo, engineAttr, busiData,true);
            retData.put("code", getSql.get("code"));
            retData.put("msg", getSql.get("msg"));
            if ("200".equals(getSql.get("code").toString())) {
                sqlStr = getSql.get("sql").toString();
                return dbAdapterService.execSqlQuery(sqlStr, busiData);
            } else {
                log.error("{} failed ... call engineGetMysqlSql is illegal ... engineNo [{}]", moduleName, engineNo);
                throw new CustomException("参数处理失败");
            }
        } else {
            log.error("{} failed ... parament engineAttr is null ... engineNo [{}]", moduleName, engineNo);
        }

        return JsonResults.error("sql执行失败");
    }
    private Object engineMysqlExecTran(List<EngineSourceVo> engineList, Map<String, Object> busiData) throws Exception {
        String moduleName = this.getClass().getSimpleName() + ".engineMysqlExecTran";
        String moduleMsg = "engine mysql query service ";
        Map<String, Object> retData = new HashMap<>();

        Map<String, Object> getSql = null;
        retData.put("code", "997");
        retData.put("msg", moduleMsg + "s system error");
        List<DoEngineDataVo> doEngineDataVoList=new ArrayList<>();
        //1、读取引擎属性
        if (!CollectionUtils.isEmpty(engineList)) {
            for (EngineSourceVo engineSourceVo : engineList) {
                Map<String, Object>  engineAttr = this.getEngineCacheInfo(engineSourceVo.getEngineNo(), busiData.get("app_id").toString());
                if (!engineAttr.containsKey("engine_info")) {
                    log.error("{}查询不到引擎信息:engineNo={}" ,moduleName, engineSourceVo.getEngineNo());
                    throw new CustomException(moduleName+"查询不到引擎信息:engineNo=" + engineSourceVo.getEngineNo());
                }
                DoEngineDataVo doEngineDataVo=new DoEngineDataVo();
                doEngineDataVo.setSeq(engineSourceVo.getSeq());
                doEngineDataVo.setEngineNo(engineSourceVo.getEngineNo());
                doEngineDataVo.setEngineAttr(engineAttr);
                //sql获取(未替换参数)
                getSql = this.engineGetSql(engineSourceVo.getEngineNo(), engineAttr, busiData,false);
                retData.put("code", getSql.get("code"));
                retData.put("msg", getSql.get("msg"));
                if ("200".equals(getSql.get("code").toString())) {
                   String sqlStr = getSql.get("sql").toString();
                    doEngineDataVo.setSql(sqlStr);
                } else {
                    log.error("{} failed ... call engineGetMysqlSql is illegal ... engin: [{}]", moduleName, engineList);
                    throw new CustomException("参数处理失败");
                }
                doEngineDataVoList.add(doEngineDataVo);
            }

            return dbAdapterService.execSqlFlows(doEngineDataVoList, busiData);
        } else {
            log.error("{} failed ... parament engineList is null ... engin: [{}]", moduleName, engineList);
        }


        return JsonResults.error("sql执行失败");
    }
    /**
     * mysql引擎Sql公共处理模块
     * 1、引擎参数读取
     * 2、引擎组参数读取
     * 3、SQL参数处理引擎组参数
     * 4、SQL引擎参数处理
     * 5、SQL业务参数值导入
     * 6、SQL检查参数变量
     * 7、返回SQL
     *
     * @param engineNo
     * @param engineAttr
     * @param busiData
     * @return
     */
    private Map<String, Object> engineGetSql(String engineNo, Map<String, Object> engineAttr, Map<String, Object> busiData,Boolean isReplace) {
        String moduleName = this.getClass().getName() + ".engineGetMysqlSql";
        String moduleMsg = "get  mysql sql from engine attr servcie";
        Map<String, Object> retData = new HashMap<>();
        Map<String, Object> engineInfo = null;

        String sqlStr = null;
        retData.put("code", "971");
        retData.put("msg", moduleMsg + "system error");
        retData.put("sql", null);
        if (!CollectionUtils.isEmpty(engineAttr)) {
            if (engineAttr.containsKey("engine_info")) {
                if (engineAttr.get("engine_info") != null) {
                    //1、引擎参数读取
                    engineInfo = (Map<String, Object>) engineAttr.get("engine_info");
                  /*  if (engineAttr.get("EnginePara") != null) {
                        enginePara = (List<Map<String, Object>>) engineAttr.get("EnginePara");
                    }*/
                   /* //2、引擎组参数读取
                    if (engineInfo.get("EngineSource") != null) {
                        engineParaGroup = this.getEngineParaGroupInfo(engineInfo.get("EngineSource").toString());
                    }*/
                    //3、SQL参数处理引擎组参数
                    if (engineInfo.get("engine_resource") != null) {
                        sqlStr = engineInfo.get("engine_resource").toString();
                        if (busiData.containsKey("callParam")) {
                            String callParam = busiData.get("callParam").toString();
                            try {
                                if (isReplace){
                                    sqlStr = MyBatisUtil.replaceSqlParam(sqlStr, callParam);
                                }
                            } catch (Exception e) {
                                log.error("sql解参数析错误:sql:{};参数:{}", sqlStr, callParam,e);
                                throw new CustomException("sql解参数析错误");
                            }


                        }
                     /*   if (!CollectionUtils.isEmpty(engineParaGroup)) {
                            for (Map<String, Object> sqlPara : engineParaGroup) {
                                if (sqlPara.containsKey("ParaType") && sqlPara.get("ParaType") != null) {
                                    if ("ReplaceAppend".equals(sqlPara.get("ParaType").toString())) {
                                        if (sqlPara.get("ParaValue") != null) {
                                            sqlStr = sqlStr.replace(sqlPara.get("ParaReferValue").toString(), sqlPara.get("ParaReferValue").toString() + sqlPara.get("ParaValue").toString());
                                        }
                                    } else if ("Append".equals(sqlPara.get("ParaType").toString())) {
                                        if (sqlPara.get("ParaValue") != null) {
                                            sqlStr = sqlStr + sqlPara.get("ParaValue").toString();
                                        }
                                    } else if ("ReplaceAppendIf".equals(sqlPara.get("ParaType").toString())) {
                                        if (sqlPara.get("ParaValue") != null) {
                                            if (sqlPara.get("ParaName") != null) {
                                                if (busiData.containsKey("apiPara")) {
                                                    Map<String, Object> apiParam = (Map<String, Object>) JSON.parseObject(busiData.get("apiPara").toString(), Map.class);

                                                    if (apiParam.get("ParaName") != null) {
                                                        if (apiParam.containsKey(sqlPara.get("ParaName").toString())) {
                                                            String sqlParaTemp = sqlPara.get("ParaValue").toString().replace("${" + sqlPara.get("ParaName").toString() + "}", apiParam.get(sqlPara.get("ParaName").toString()).toString());
                                                            sqlStr = sqlStr.replace(sqlPara.get("ParaReferValue").toString(), sqlPara.get("ParaReferValue").toString() + sqlParaTemp);

                                                        }
                                                    }


                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //4、SQL引擎参数处理

                         if (!CollectionUtils.isEmpty(enginePara)) {
                            for (Map<String, Object> sqlPara : enginePara) {
                                if (sqlPara.containsKey("EngineParaGroupNo")) {
                                    if ("SQL_STR".equals(sqlPara.get("EngineParaGroupNo").toString())) {
                                        if (sqlPara.containsKey("EngineParaOutRe")) {
                                            if ("INSERT".equals(sqlPara.get("EngineParaOutRe").toString())) {
                                                sqlStr = sqlPara.get("EngineParaOutRe").toString() + " " + sqlStr;
                                            } else if ("APPEND".equals(sqlPara.get("EngineParaOutRe").toString())) {
                                                sqlStr = sqlStr + " " + sqlPara.get("EngineParaOutRe").toString();
                                            } else if ("REPALCE".equals(sqlPara.get("EngineParaOutRe").toString())) {
                                                sqlStr = sqlStr.replace("${" + sqlPara.get("EngineParaName").toString() + "}", sqlPara.get("EngineParaOutRe").toString());
                                            } else {
                                                log.error("{} failed ... enginePara EngineParaType is illegal ... EngineNO [{}] EngineParaType [{}] \n", moduleName, engineNo, sqlPara.get("EngineParaType").toString());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //5、SQL业务参数值导入
                        if (busiData.containsKey("ServiceParament")) {
                            if (busiData.get("ServiceParament") != null) {
                                List<String> paramentList = JSON.parseArray(JSON.toJSONString(busiData.get("ServiceParament")), String.class);
                                for (String servicePara : paramentList) {
                                    if (busiData.containsKey("apiPara")) {
                                        Map apiPara = JSON.parseObject(JSON.toJSONString(busiData.get("apiPara")), Map.class);
                                        if (apiPara.get(servicePara) != null) {
                                            sqlStr = sqlStr.replace("${" + servicePara + "}", apiPara.get(servicePara).toString());

                                        } else {
                                            sqlStr = sqlStr.replace("${" + servicePara + "}", "");
                                        }
                                    }
                                }
                            } else {
                                log.error("{} failed ... get data from dbBusiness ServiceParament is None ... EngineNO [{}] retMsg [{}]", moduleName, engineNo, retData);
                            }
                        } else {
                            log.error("{} failed ... get data from dbBusiness ServiceParament is null ... EngineNO [{}] sqlStr [{}]", moduleName, engineNo, sqlStr);
                        }
                    } else {
                        log.error("{} failed ... parament engineAttr engine_info['EngineResource'] is null ... engineNo [{}]", moduleName, engineNo);
                    }*/

                    } else {
                        log.error("{} failed ... parament engineAttr engine_info is null ... engineNo [{}]", moduleName, engineNo);
                    }
                } else {
                    log.error("{} failed ... parament engineAttr engine_info not found ... engineNo [{}]", moduleName, engineNo);
                }

            } else {
                log.error("{} failed ... parament engineAttr is null ", moduleName);
            }
        }
        if (StringUtils.isNotEmpty(sqlStr)) {
            if (sqlStr.contains("${")) {
                retData.put("code", "972");
                retData.put("msg", moduleMsg + "... variable error" + sqlStr);
                log.error("{} failed ... variable has null ... engineNo [{}] [{}]\n", moduleName, engineNo, sqlStr);
            } else {
                retData.put("code", "200");
                retData.put("msg", moduleMsg + "... success");
                retData.put("sql", sqlStr);
            }
        } else {
            log.error("{} failed ... parament engineAttr is null ... engineNo [{}]", moduleName, engineNo);
        }

        //7、返回SQL
        return retData;

    }



    private List<Map<String, Object>> getEngineParaGroupInfo(String paraGroupNo) {
        String moduleName = this.getClass().getName() + ".getEngineParaGroupInfo";
//        String moduleMsg = "get engine paraments group info from module cache";
        List<Map<String, Object>> retData = new ArrayList<>();
        log.debug("{} Begin to deal ParaGroupNo [{}]", moduleName, paraGroupNo);
        if (StringUtils.isNotEmpty(paraGroupNo)) {
            String paraKeyScan = "DATA_API_MODULE_PARA_INFO:*:" + paraGroupNo;
            String[] paraKeyList = {"ParaNo", "ParaGroupNo", "ParaName", "ParaStatus", "ParaType", "ParaReferValue", "ParaValue", "ParaGroupName", "ParentParaNo", "ParaLevel", "ParaDesc", "ParaNote"};
            Collection<String> scanKeyList = redisService.keys(paraKeyScan);
            if (!CollectionUtils.isEmpty(scanKeyList)) {
                for (String paraKey : scanKeyList) {
                    Map<String, Object> para = new HashMap<>();
                    for (String key : paraKeyList) {
                        Object value = redisService.getCacheMapValue(paraKey, key);
                        if (value != null) {
                            para.put(key, value.toString().replace("\\x00", " "));
                        }
                    }
                    if (para.get("ParaNo") != null) {
                        retData.add(para);
                    }
                }
            }
        }
        log.debug("{} Finish to deal ParaGroupNo [{}] \n", moduleName, paraGroupNo);
        return retData;
    }

    private Map<String, Object> getEngineCacheInfo(String engineNo, String appId) {
        String moduleName = this.getClass().getName() + ".getEngineCacheInfo";
        String moduleMsg = "get engine info from cache ";
        Map<String, Object> retData = new HashMap<>();
        retData.put("code", "997");
        retData.put("msg", moduleMsg + "system error");
        String engineKey = "data_api_engine_info:" + engineNo+":"+appId;
        Map<String, String> cacheMap = redisService.getCacheMap(engineKey);
        if (CollectionUtils.isEmpty(cacheMap)){
            throw new CustomException("查询不到引擎数据");
        }
        for (String key : cacheMap.keySet()) {
            String value = cacheMap.get(key);
            if (value != null) {
                cacheMap.put(key, value.replace("\\x00", " "));
            }
        }
            retData.put("engine_info", cacheMap);
            retData.put("code", "200");
            retData.put("msg", moduleMsg + " system success ... Engine " + engineNo);

      /*  String engineKeyScan = "data_api_engine_para_info:" + engineNo +":"+appId;
        Collection<String> cacheHashByKeys = redisService.keys(engineKeyScan);
        if (!CollectionUtils.isEmpty(cacheHashByKeys)) {
            List<Map<String, String>> engineParaList = new ArrayList<>();
            for (String cacheHashByKey : cacheHashByKeys) {
                Map<String, String> paramMap = redisService.getCacheMap(cacheHashByKey);
                for (String key : paramMap.keySet()) {
                    String value =paramMap.get(key);
                    if (value != null) {
                        paramMap.put(key, value.replace("\\x00", " "));
                    }
                }
                engineParaList.add(paramMap);
            }
            retData.put("engine_para", engineParaList);
        }*/
        log.debug("{} Finish to deal EngineNO [{}] \n", moduleName, engineNo);
        return retData;
    }

    /**
     * 读取API缓存信息
     * 1、API信息查询re
     * 2、查询redis指定
     * 3、API信息中服务
     * 4、API信息中服务
     * 5、API参数信息查
     * 6、查询redis指定
     * 7、循环查询redis
     * 8、API参数信息中
     * 9、返回读取结果
     *
     * @param appId
     * @param apiId
     * @param version
     * @return
     */
    private Map<String, Object> getApiCacheInfo(String appId, String apiId, String version) {
        String moduleMsg = "get api info from cache";
        Map<String, Object> retData = new HashMap<>();
        // key形式: DATA_API_INFO:chkColMeteringDay_0001:dataStation
        //api信息
        String apiKey = "data_api_info:" + apiId + "_" + version + ":" + appId;
        String[] keyList = {"service_id", "app_id", "api_id", "api_version", "api_path", "service_type", "service_name", "service_resource",  "service_desc", "start_date", "end_date"};
        String startDate = null;
        String endDate = null;
        Map<String, String> cacheMap = redisService.getCacheMap(apiKey);
        for (String key : keyList) {
            String value = cacheMap.get(key);
            if (StringUtils.isEmpty(value)) {
                retData.put(key, null);
                continue;
            }
            value= value.replace("\\x00", " ");
            if ("start_date".equals(key)) {
                startDate = value;
            }
            if ("end_date".equals(key) ) {
                endDate = value;
            }
            retData.put(key, value);
        }
        if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
            if (!DateUtils.currDateIsInDateRange(startDate, endDate)) {
                throw new CustomException("api发布信息不在有效期范围内");
            }
        }
        //参数信息3 请求参数 4返回参数
        String apiReqParamKeyScan = "data_api_para_info:" + apiId + "_" + version + ":" + appId + ":"+ApiInfoConstant.API_PARA_TYPE_3;
        String[] apiKeyList = {"service_id", "app_id", "para_group_no", "para_id", "para_type", "para_status", "para_name", "para_resource", "para_values", "para_desc"};
        Map<String, String> paraMap = redisService.getCacheMap(apiReqParamKeyScan);
        if (!CollectionUtils.isEmpty(paraMap)) {
            Map<String, Object> apiPara = new HashMap<>();
            for (String key : apiKeyList) {
                String value = paraMap.get(key);
                if (StringUtils.isNotEmpty(value)) {
                    String replace = value.replace("\\x00", " ");
                    apiPara.put(key, replace);
                } else {
                    apiPara.put(key, null);
                }
            }
            retData.put(ApiInfoConstant.API_PARA, apiPara);
        }

        //参数信息3 请求参数 4返回参数
        String apiRepParamKeyScan = "data_api_para_info:" + apiId + "_" + version + ":" + appId + ":"+ApiInfoConstant.API_PARA_TYPE_4;
        Map<String, String> repParaMap = redisService.getCacheMap(apiRepParamKeyScan);
        if (!CollectionUtils.isEmpty(repParaMap)) {
            Map<String, Object> apiPara = new HashMap<>();
            for (String key : apiKeyList) {
                String value = repParaMap.get(key);
                if (StringUtils.isNotEmpty(value)) {
                    String replace = value.replace("\\x00", " ");
                    apiPara.put(key, replace);
                } else {
                    apiPara.put(key, null);
                }
            }
            retData.put(ApiInfoConstant.API_RESPONSE_PARA, apiPara);
        }
        //应用信息
        String applicationKey = "data_api_app_info:" + appId;
        String[] applicationKeyList = {"app_id", "app_name", "secret_key", "token_expiration", "open_ip_white_list", "ip_white_list", "auth_start_date", "auth_end_date"};
        Map<String, String> appMap = redisService.getCacheMap(applicationKey);
        if (!CollectionUtils.isEmpty(appMap)){
            Map<String, Object> apiPara = new HashMap<>();
            for (String key : applicationKeyList) {
                String value = appMap.get(key);
                if (StringUtils.isNotEmpty(value)) {
                    String replace = value.replace("\\x00", " ");
                    apiPara.put(key, replace);
                } else {
                    apiPara.put(key, null);
                }
            }
            retData.put("application", apiPara);
        }else {
            throw new CustomException("应用信息不存在");
        }

        if (retData.get("service_id") == null) {
            retData.put("code", "996");
            retData.put("msg", moduleMsg + "system error ... API " + appId + "/" + apiId + " not found ");
            return retData;
        } else {
            retData.put("code", "200");
            retData.put("msg", moduleMsg + "system success ... API" + appId + "/" + apiId);
        }
        return retData;
    }

    @Override
    public String authorize(String applicationCode, String secret) throws Exception {
        String authorizeKey = RedisKeyConstant.API_AUTHORIZE_FAILED_COUNT_KEY + applicationCode;
        String authorizeDayLokKey = RedisKeyConstant.API_AUTHORIZE_FAILED_COUNT_KEY + "total:" + applicationCode;
        RedisAtomicInteger dayLockCounter = RedisUtil.getRedisCounter(authorizeDayLokKey, 1, TimeUnit.DAYS);
        RedisAtomicInteger authorizeCounter = RedisUtil.getRedisCounter(authorizeKey, freezeTime, TimeUnit.MINUTES);
        if (authorizeCounter.get() >= failedRetryCount) {
            int thPower = dayLockCounter.get() + 1 - failedRetryCount == 0 ? 1 : dayLockCounter.get() + 1 - failedRetryCount;
            int expireTime = (int) Math.pow(Double.valueOf(freezeTime), Double.valueOf(thPower));
            authorizeCounter.getAndIncrement();
            authorizeCounter.expire(expireTime, TimeUnit.MINUTES);
            dayLockCounter.getAndIncrement();
            Long remainExpireTime = RedisUtil.stringRedisTemplate.getExpire(authorizeKey, TimeUnit.SECONDS);
            remainExpireTime = remainExpireTime % 60 == 0 ? remainExpireTime / 60 : remainExpireTime / 60 + 1;
            log.error("客户端ip：{}，应用编号：{}，今日总失败重试次数达：{}次", IpUtil.getIpAddr(), applicationCode, dayLockCounter.get());
            throw new CustomException("失败重试次数达" + authorizeCounter.get() + "次，已达上限，请于" + remainExpireTime + "分钟后重试");
        }
        int remainRetryCount = failedRetryCount - (authorizeCounter.get() + 1);
        Map<String, Object> application = redisService.getCacheMap("data_api_app_info:" + applicationCode);

        if (CollectionUtils.isEmpty(application)) {
            authorizeCounter.getAndIncrement();
            dayLockCounter.getAndIncrement();
            throw new CustomException("应用不存在，剩余重试次数" + remainRetryCount);
        } else if (Integer.parseInt(application.get("app_status").toString()) == 0) {
            authorizeCounter.getAndIncrement();
            dayLockCounter.getAndIncrement();
            throw new CustomException("应用被禁用不存在，剩余重试次数" + remainRetryCount);
        } else if (!secret.trim().equals(application.get("secret_key").toString())) {
            authorizeCounter.getAndIncrement();
            dayLockCounter.getAndIncrement();
            throw new CustomException("秘钥错误，剩余重试次数，剩余重试次数" + remainRetryCount);
        }
        if (application.containsKey("auth_start_date") && application.containsKey("auth_end_date")) {
            String effective_start_date = application.get("auth_start_date").toString();
            String effective_end_date = application.get("auth_end_date").toString();
            Date startDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, effective_start_date);
            Date endDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, effective_end_date);
            if (!DateUtil.currDateIsInDateRange(startDate, endDate)) {
                throw new CustomException("应用日期不在有效范围内");
            }
        }
        Long tokenExpiration = null;
        if (application.get("token_expiration") != null) {
            tokenExpiration = Long.parseLong(application.get("token_expiration").toString()) * 24 * 60 * 60 * 1000;
        }
        String token = CallApiJwtUtil.sign(applicationCode, tokenExpiration);
        return token;
    }


    @Override
    @PostConstruct
    public JsonResults innitResource() {
        String dataKey = "data_api_source_info:*";
        String paramPreKey = "data_api_source_para_info";
        Collection<String> keys = redisService.keys(dataKey);
        if (!CollectionUtils.isEmpty(keys)) {
            for (String key : keys) {
                Map<String, Object> cacheMap = redisService.getCacheMap(key);
                if (!cacheMap.containsKey("app_id")){
                    continue;
                }
                if (!cacheMap.containsKey("source_id")){
                    continue;
                }
                String source_id = cacheMap.get("source_id").toString();
                String app_id = cacheMap.get("app_id").toString();
                if (cacheMap.containsKey("source_adapter_type")) {
                    String adapter = cacheMap.get("source_adapter_type").toString();
                    String paramEndKey = app_id + ":" + env_type + ":" + source_id;
                    String url = redisService.getCacheMapValue(paramPreKey + ":url:" + paramEndKey, "para_value");
                    if (StringUtils.isEmpty(url)) {
                        continue;
                    }
                    url = url.replace("\\x00", " ");
                    String username = redisService.getCacheMapValue(paramPreKey + ":username:" + paramEndKey, "para_value");
                    username = username.replace("\\x00", " ");
                    String password = redisService.getCacheMapValue(paramPreKey + ":password:" + paramEndKey, "para_value");
                    password = password.replace("\\x00", " ");

                    String is_secret = redisService.getCacheMapValue(paramPreKey + ":password:" + paramEndKey, "is_secret");
                    if ("Y".equals(is_secret)) {
                        if (cacheMap.get("source_secret_key") != null) {
                            String secretKey = cacheMap.get("source_secret_key").toString();
                            password= AESUtil.decrypt(password,secretKey);
                            if (password==null){
                                log.error("password解密失败:{}",paramEndKey);
                              return   JsonResults.error("password解密失败:"+paramEndKey);
                            }
                        }
                    }
                    String redisKey = "data_api_source_info:" + app_id + ":" + adapter+":"+env_type;
                    if (ApiInfoConstant.ADAPTER_MYSQL.equals(adapter)) {
                        String driver = redisService.getCacheMapValue(paramPreKey + ":driver:" + paramEndKey, "para_value");
                        driver = driver.replace("\\x00", " ");
                        ConcurrentHashMap<String, DataSource> dataSourceMap = JDBCUtilsDruid.getDataSourceMap();

                        Map<String, Object> map2 = new HashMap();
                        //设置URL
                        map2.put(DruidDataSourceFactory.PROP_URL, url);
                        //设置驱动Driver
                        map2.put(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, driver);
                        //设置用户名
                        map2.put(DruidDataSourceFactory.PROP_USERNAME, username);
                        //设置密码
                        map2.put(DruidDataSourceFactory.PROP_PASSWORD, password);
                        //连接池大小
                        map2.put(DruidDataSourceFactory.PROP_MAXACTIVE, "100");
                        map2.put(DruidDataSourceFactory.PROP_INITIALSIZE, "10");
                        //每次增加的连接数
                        map2.put(DruidDataSourceFactory.PROP_MINIDLE, "10");
                        // 设置最大等待时间为10秒
                        map2.put(DruidDataSourceFactory.PROP_MAXWAIT, "10000");
                        map2.put(DruidDataSourceFactory.PROP_VALIDATIONQUERY, "SELECT 1");
                        map2.put(DruidDataSourceFactory.PROP_TESTONBORROW,"false");
                        map2.put(DruidDataSourceFactory.PROP_TESTONRETURN,"false");
                        map2.put(DruidDataSourceFactory.PROP_TESTWHILEIDLE,"true");

                        //设置其余参数 看需求配置
                        try {
                            if (dataSourceMap.containsKey(redisKey)) {
                                DruidDataSource dataSource1 = (DruidDataSource) dataSourceMap.get(redisKey);
                                dataSource1.close();
                            }
                            DataSource dataSource = DruidDataSourceFactory.createDataSource(map2);
                            dataSourceMap.put(redisKey, dataSource);
                        } catch (Exception e) {
                            log.error("innitResource,数据源初始化失败", e);
                            return JsonResults.error("数据源初始化失败");
                        }
                    }
                } else {
                    return JsonResults.error("未声明数据源驱动类型");
                }
            }
        }


        return JsonResults.success("初始化连接池成功");
    }
    public JsonResults innitResourceByAppId1(String appId) {
        String dataKey = "data_api_source_info:*";
        String paramPreKey = "data_api_source_para_info";
        Collection<String> keys = redisService.keys(dataKey);
        if (!CollectionUtils.isEmpty(keys)) {
            for (String key : keys) {
                Map<String, Object> cacheMap = redisService.getCacheMap(key);
                if (!cacheMap.containsKey("app_id")) {
                    continue;
                }
                if (StringUtils.isNotEmpty(appId) && !cacheMap.get("app_id").toString().equals(appId)) {
                    continue;
                }
                if (!cacheMap.containsKey("source_id")) {
                    continue;
                }
                String source_id = cacheMap.get("source_id").toString();
                String app_id = cacheMap.get("app_id").toString();
                if (cacheMap.containsKey("source_adapter_type")) {
                    String adapter = cacheMap.get("source_adapter_type").toString();
                    String paramEndKey = app_id + ":" + env_type + ":" + source_id;
                    String url = redisService.getCacheMapValue(paramPreKey + ":url:" + paramEndKey, "para_value");
                    if (StringUtils.isEmpty(url)) {
                        continue;
                    }
                    url = url.replace("\\x00", " ");
                    String username = redisService.getCacheMapValue(paramPreKey + ":username:" + paramEndKey, "para_value");
                    username = username.replace("\\x00", " ");
                    String password = redisService.getCacheMapValue(paramPreKey + ":password:" + paramEndKey, "para_value");
                    password = password.replace("\\x00", " ");

                    String is_secret = redisService.getCacheMapValue(paramPreKey + ":password:" + paramEndKey, "is_secret");
                    if ("Y".equals(is_secret)) {
                        if (cacheMap.get("source_secret_key") != null) {
                            String secretKey = cacheMap.get("source_secret_key").toString();
                            password = AESUtil.decrypt(password, secretKey);
                            if (password == null) {
                                log.error("password解密失败:{},appId={}", paramEndKey, appId);
                                return JsonResults.error("password解密失败");
                            }
                        }
                    }
                    String redisKey = "data_api_source_info:" + app_id + ":" + adapter + ":" + env_type;
                    if (ApiInfoConstant.ADAPTER_MYSQL.equals(adapter)) {
                        String driver = redisService.getCacheMapValue(paramPreKey + ":driver:" + paramEndKey, "para_value");
                        driver = driver.replace("\\x00", " ");
                        ConcurrentHashMap<String, DataSource> dataSourceMap = JDBCUtilsHakari.getDataSourceMap();

                        try {
                            if (dataSourceMap.containsKey(redisKey)) {
                                HikariDataSource dataSource =(HikariDataSource) dataSourceMap.get(redisKey);
                                if (dataSource!=null){
                                    dataSource.close();
                                }
                            }
                            HikariConfig sourceConfig = getDataSourceConfig(username, password, url, driver, redisKey);
                            HikariDataSource dataSource = new HikariDataSource(sourceConfig);
                            dataSourceMap.put(redisKey, dataSource);
                        } catch (Exception e) {
                            log.error("innitResourceByAppId,appId={},数据源初始化失败", appId, e);
                            return JsonResults.error("数据源初始化失败");
                        }
                    }
                } else {
                    log.error("innitResourceByAppId,appId={},未声明数据源驱动类型", appId);
                    return JsonResults.error("未声明数据源驱动类型");
                }
            }
        }

        return JsonResults.success("数据源初始化成功");

    }
    @Override
    public JsonResults innitResourceByAppId(String appId) {
        String dataKey = "data_api_source_info:*";
        String paramPreKey = "data_api_source_para_info";
        Collection<String> keys = redisService.keys(dataKey);
        if (!CollectionUtils.isEmpty(keys)) {
            for (String key : keys) {
                Map<String, Object> cacheMap = redisService.getCacheMap(key);
                if (!cacheMap.containsKey("app_id")) {
                    continue;
                }
                if (StringUtils.isNotEmpty(appId) && !cacheMap.get("app_id").toString().equals(appId)) {
                    continue;
                }
                if (!cacheMap.containsKey("source_id")) {
                    continue;
                }
                String source_id = cacheMap.get("source_id").toString();
                String app_id = cacheMap.get("app_id").toString();
                if (cacheMap.containsKey("source_adapter_type")) {
                    String adapter = cacheMap.get("source_adapter_type").toString();
                    String paramEndKey = app_id + ":" + env_type + ":" + source_id;
                    String url = redisService.getCacheMapValue(paramPreKey + ":url:" + paramEndKey, "para_value");
                    if (StringUtils.isEmpty(url)) {
                        continue;
                    }
                    url = url.replace("\\x00", " ");
                    String username = redisService.getCacheMapValue(paramPreKey + ":username:" + paramEndKey, "para_value");
                    username = username.replace("\\x00", " ");
                    String password = redisService.getCacheMapValue(paramPreKey + ":password:" + paramEndKey, "para_value");
                    password = password.replace("\\x00", " ");

                    String is_secret = redisService.getCacheMapValue(paramPreKey + ":password:" + paramEndKey, "is_secret");
                    if ("Y".equals(is_secret)) {
                        if (cacheMap.get("source_secret_key") != null) {
                            String secretKey = cacheMap.get("source_secret_key").toString();
                            password = AESUtil.decrypt(password, secretKey);
                            if (password == null) {
                                log.error("password解密失败:{},appId={}", paramEndKey, appId);
                                return JsonResults.error("password解密失败");
                            }
                        }
                    }
                    String redisKey = "data_api_source_info:" + app_id + ":" + adapter + ":" + env_type;
                    if (ApiInfoConstant.ADAPTER_MYSQL.equals(adapter)) {
                        String driver = redisService.getCacheMapValue(paramPreKey + ":driver:" + paramEndKey, "para_value");
                        driver = driver.replace("\\x00", " ");
                        ConcurrentHashMap<String, DataSource> dataSourceMap = JDBCUtilsDruid.getDataSourceMap();

                        Map<String, Object> map2 = new HashMap();
                        //设置URL
                        map2.put(DruidDataSourceFactory.PROP_URL, url);
                        //设置驱动Driver
                        map2.put(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, driver);
                        //设置用户名
                        map2.put(DruidDataSourceFactory.PROP_USERNAME, username);
                        //设置密码
                        map2.put(DruidDataSourceFactory.PROP_PASSWORD, password);
                        //连接池大小
                        map2.put(DruidDataSourceFactory.PROP_MAXACTIVE, "100");
                        map2.put(DruidDataSourceFactory.PROP_INITIALSIZE, "10");
                        //每次增加的连接数
                        map2.put(DruidDataSourceFactory.PROP_MINIDLE, "10");
                        // 设置最大等待时间为10秒
                        map2.put(DruidDataSourceFactory.PROP_MAXWAIT, "10000");
                        map2.put(DruidDataSourceFactory.PROP_VALIDATIONQUERY, "SELECT 1");
                        map2.put(DruidDataSourceFactory.PROP_TESTONBORROW,"false");
                        map2.put(DruidDataSourceFactory.PROP_TESTONRETURN,"false");
                        map2.put(DruidDataSourceFactory.PROP_TESTWHILEIDLE,"true");

                        //设置其余参数 看需求配置
                        try {
                            if (dataSourceMap.containsKey(redisKey)) {
                                DruidDataSource dataSource1 = (DruidDataSource) dataSourceMap.get(redisKey);
                                dataSource1.close();
                            }
                            DataSource dataSource = DruidDataSourceFactory.createDataSource(map2);
                            dataSourceMap.put(redisKey, dataSource);
                        } catch (Exception e) {
                            log.error("innitResourceByAppId,appId={},数据源初始化失败", appId, e);
                            return JsonResults.error("数据源初始化失败");
                        }
                    }
                } else {
                    log.error("innitResourceByAppId,appId={},未声明数据源驱动类型", appId);
                    return JsonResults.error("未声明数据源驱动类型");
                }
            }
        }

        return JsonResults.success("数据源初始化成功");

    }
    public HikariConfig getDataSourceConfig(String userName, String password, String url, String driver, String redisKey) {
        HikariConfig  config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(userName);
        config.setPassword(password);
        config.setMinimumIdle(10);
        config.setMaximumPoolSize(100);
        config.setConnectionTimeout(10000);
        config.setPoolName(redisKey);
        config.setMaxLifetime(500000);
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);
        return config;
    }

    @Override
    public JsonResults getMetaData(LoadApiDataToRedisParam param) {
      List<LinkedHashMap> list=  baseMapper.getMetaData(param);
      if (!CollectionUtils.isEmpty(list)){
          for (LinkedHashMap map : list) {
              String column_name = map.get("column_name").toString();
              map.put("column_name",StringUtils.toCamelCase(column_name));
          }
      }

        return JsonResults.success(list);
    }

}
