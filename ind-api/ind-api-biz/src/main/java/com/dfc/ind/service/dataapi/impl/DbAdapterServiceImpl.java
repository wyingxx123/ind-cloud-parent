package com.dfc.ind.service.dataapi.impl;


import com.alibaba.fastjson.JSONObject;
import com.dfc.ind.common.core.exception.CustomException;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.redis.service.RedisService;
import com.dfc.ind.entity.dataapi.param.BasePageQueryParam;
import com.dfc.ind.entity.dataapi.param.ExecuteQuerySqlParam;
import com.dfc.ind.entity.dataapi.vo.DoEngineDataVo;
import com.dfc.ind.service.dataapi.IDbAdapterService;
import com.dfc.ind.service.dataapi.IDbMysqlService;
import com.dfc.ind.service.dataapi.IDbSequoiaService;
import com.dfc.ind.uitls.ApiInfoConstant;
import com.dfc.ind.uitls.BasePageResult;
import com.dfc.ind.uitls.SqlToJBsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

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
public class DbAdapterServiceImpl implements IDbAdapterService {
    @Autowired
    private IDbMysqlService dbMysqlService;

    @Autowired
    private IDbSequoiaService dbSequoiaService;

    @Autowired
    private RedisService redisService;

    @Value("${env.type:DEV}")
    private String env_type;


    @Override
    public BasePageResult execSqlQuery(String sqlStr, Map<String, Object> busiData) throws Exception {
        //check data from database by dbDict
        String moduleName = this.getClass().getSimpleName() + ".execSqlQuery";
        BasePageResult<LinkedHashMap<String, Object>> pageResult = null;
        if (StringUtils.isNotEmpty(sqlStr)) {
            if (busiData.containsKey("application")) {
                Map<String, Object> applicationMap = (Map<String, Object>) busiData.get("application");
                if (applicationMap.containsKey("app_id")) {
                    String app_id = applicationMap.get("app_id").toString();
                    ExecuteQuerySqlParam querySqlParam = new ExecuteQuerySqlParam();
                    querySqlParam.setSql(sqlStr);
                    //设置分页参数
                    String callParam = busiData.get("callParam").toString();
                    setPageQueryParam(callParam, querySqlParam);
                    //sql关键字转大写
                    sqlStr=SqlToJBsonUtils.sqlToUpperCase(sqlStr);
                    querySqlParam.setSql(sqlStr);
                    String datasourceId="data_api_source_info:"+app_id+":"+ApiInfoConstant.ADAPTER_MYSQL+":"+env_type;
                    querySqlParam.setDataSourceId(datasourceId);
//                    log.info("执行mysql:{}",sqlStr);
                    return dbMysqlService.executeQuerySql(querySqlParam);
                }
            } else {
                throw new CustomException("应用不存在");
            }

        } else {
            throw new CustomException("sql为空");
        }
        return pageResult;
    }



    private void setPageQueryParam(String queryParam, BasePageQueryParam pageQueryParam) {
        JSONObject obj = (JSONObject) JSONObject.parse(queryParam);
        if (obj != null) {
            if (obj.get(ApiInfoConstant.PAGE_SIZE) != null && !org.springframework.util.StringUtils.isEmpty(obj.get(ApiInfoConstant.PAGE_SIZE))) {
                pageQueryParam.setPageSize(Integer.parseInt(obj.getString(ApiInfoConstant.PAGE_SIZE)));
            }
            if (obj.get(ApiInfoConstant.PAGE_INDEX) != null && !org.springframework.util.StringUtils.isEmpty(obj.get(ApiInfoConstant.PAGE_INDEX))) {
                pageQueryParam.setPageIndex(Integer.parseInt(obj.getString(ApiInfoConstant.PAGE_INDEX)));
            }
        }
    }

    @Override
    public int execSqlExec(String sqlStr, Map<String, Object> busiData) {
        String moduleName = this.getClass().getSimpleName() + ".execSqlExec";
        if (busiData.containsKey("application")) {
            Map<String, Object> applicationMap = (Map<String, Object>) busiData.get("application");

            if (applicationMap.containsKey("app_id")) {
                String app_id = applicationMap.get("app_id").toString();
                ExecuteQuerySqlParam updateSqlParam = new ExecuteQuerySqlParam();
                updateSqlParam.setSql(sqlStr);
                //sql关键字转大写
                sqlStr=SqlToJBsonUtils.sqlToUpperCase(sqlStr);
                //判断执行mysql还是sequoia_db
                String datasourceId="data_api_source_info:"+app_id+":"+ApiInfoConstant.ADAPTER_MYSQL+":"+env_type;
                return dbMysqlService.execUpdate(sqlStr, datasourceId);
            }
        } else {
            throw new CustomException("应用未分配数据源");
        }

        throw new CustomException("execSqlExec:不支持的引擎类型");
    }

    @Override
    public Object execSqlFlows(List<DoEngineDataVo> doEngineDataVoList, Map<String, Object> busiData) {

        String moduleName = this.getClass().getSimpleName() + ".execSqlExec";
        log.debug("{} Begin to deal doEngineDataVoList= {}", moduleName, doEngineDataVoList);
        if (busiData.containsKey("application")) {
            Map<String, Object> applicationMap = (Map<String, Object>) busiData.get("application");
            if (applicationMap.containsKey("app_id")) {
                String app_id = applicationMap.get("app_id").toString();
                String datasourceId="data_api_source_info:"+app_id+":"+ApiInfoConstant.ADAPTER_MYSQL+":"+env_type;
                return dbMysqlService.execFlowSql(doEngineDataVoList, datasourceId,busiData);
            } else {
                throw new CustomException("数据源唯一标识为空");
            }
        } else {
            throw new CustomException("应用未分配数据源");
        }

    }


}
