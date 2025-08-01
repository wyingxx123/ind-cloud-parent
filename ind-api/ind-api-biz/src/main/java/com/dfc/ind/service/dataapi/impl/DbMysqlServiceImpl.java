package com.dfc.ind.service.dataapi.impl;


import com.alibaba.fastjson.JSONObject;
import com.dfc.ind.common.core.exception.CustomException;
import com.dfc.ind.entity.dataapi.param.BasePageQueryParam;
import com.dfc.ind.entity.dataapi.param.ExecuteQuerySqlParam;
import com.dfc.ind.entity.dataapi.vo.DoEngineDataVo;
import com.dfc.ind.service.dataapi.IDbMysqlService;
import com.dfc.ind.uitls.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;
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
public class DbMysqlServiceImpl implements IDbMysqlService {

    @Override
    public int execUpdate(String sqlStr, String data_source_id) {
        Connection connection = null;
        //执行SQL
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = JDBCUtilsDruid.getConnection(data_source_id);
            if (connection == null) {
                throw new CustomException("获取不到连接信息");
            }
            preparedStatement = connection.prepareStatement(sqlStr);
            return preparedStatement.executeUpdate();

        }catch (SQLIntegrityConstraintViolationException e) {
            log.error("sql执行失败", e);
            throw new CustomException("sql执行失败:"+e.getMessage());
        } catch (Exception e) {
            log.error("sql执行失败", e);
            throw new CustomException("sql执行失败");

        } finally {
            JDBCUtilsDruid.close(resultSet, preparedStatement, connection);
        }

    }

    @Override
    public BasePageResult<LinkedHashMap<String, Object>> executeQuerySql(ExecuteQuerySqlParam param) throws Exception {
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement pstmt = null;
        List<LinkedHashMap<String, Object>> resultList = new ArrayList<>();
        String sql = param.getSql();
        String countSql = "select count(1) from (" + sql + ") a";
        long totalCount = 0;
        boolean hasPage = false;
        final String limit = " limit";
        try {
            connection = JDBCUtilsDruid.getConnection(param.getDataSourceId());
            if (connection == null) {
                throw new CustomException("获取不到连接信息");
            }
            if (!sql.contains(limit) && param.getIsPage() != null && param.getIsPage()) {
                hasPage = true;
                PreparedStatement preparedStatement = connection.prepareStatement(countSql);
                log.info("countSql:"+countSql);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    totalCount = rs.getLong(1);
                }
                sql += " limit " + (param.getPageIndex() - 1) * param.getPageSize() + "," + param.getPageSize();
                preparedStatement.close();
                rs.close();
            }
            boolean hasData = !hasPage || (hasPage && totalCount > 0);
            if (hasData) {
                pstmt = connection.prepareStatement(sql);
                resultSet = pstmt.executeQuery();
                log.info("querySql:"+countSql);
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    LinkedHashMap<String, Object> map = new LinkedHashMap<>(16);
                    for (int i = 1; i <= columnCount; i++) {
                        String label = metaData.getColumnLabel(i);
                        map.put(label, resultSet.getString(i));
                    }
                    resultList.add(map);
                }
            }
            if (!hasPage) {
                totalCount = resultList.size();
            }
        }  catch (Exception e) {
            log.error("执行sql异常：{}", sql);
            throw new CustomException(e.getMessage());
        } finally {
            JDBCUtilsDruid.close(resultSet, pstmt, connection);
        }
        return new BasePageResult<>(resultList, totalCount);
    }

    @Override
    public Object execFlowSql(List<DoEngineDataVo> doEngineDataVoList, String sourceKey, Map<String, Object> busiData) {
        Object res = null;
        Connection connection = null;
        //执行SQL
        PreparedStatement preparedStatement = null;
        //影响的行数
        int rows = 0;
        try {
            connection = JDBCUtilsDruid.getConnection(sourceKey);
            if (connection == null) {
                throw new CustomException("获取不到连接信息");
            }
            connection.setAutoCommit(false);
            //多sql排序
            doEngineDataVoList.sort(new Comparator<DoEngineDataVo>() {
                @Override
                public int compare(DoEngineDataVo o1, DoEngineDataVo o2) {
                    if (o1.getSeq() - o2.getSeq() > 0) { //变成 < 可以变成递减排序
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
            String callParam = null;
            if (busiData.containsKey("callParam")) {
                callParam = busiData.get("callParam").toString();
            }
            //引擎校验和参数替换
            for (DoEngineDataVo doEngineDataVo : doEngineDataVoList) {
                String sqlStr = doEngineDataVo.getSql();
                try {
                    sqlStr = MyBatisUtil.replaceSqlParam(sqlStr, callParam);
                } catch (Exception e) {
                    log.error("sql解参数析错误:sql:{};参数:{}", sqlStr, callParam);
                    throw new CustomException("sql解参数析错误");
                }
                if (doEngineDataVo.getEngineAttr().containsKey("engine_info")) {
                    Map<String, Object> engineAttr = doEngineDataVo.getEngineAttr();
                    Map<String, Object> engineInfo = (Map<String, Object>) engineAttr.get("engine_info");
                    if (engineInfo.containsKey("engine_type")) {
                        if ("SQL-EXEC".equals(engineInfo.get("engine_type").toString())) {
                            doEngineDataVo.setSql(sqlStr);
                            doEngineDataVo.setSqlType("EXEC");
                        } else if ("SQL-QUERY".equals(engineInfo.get("engine_type").toString())) {
                            ExecuteQuerySqlParam querySqlParam = new ExecuteQuerySqlParam();
                            //设置分页参数
                            setPageQueryParam(callParam, querySqlParam);
                            querySqlParam.setIsPage(true);
                            doEngineDataVo.setSql(sqlStr);
                            doEngineDataVo.setQuerySqlParam(querySqlParam);
                            doEngineDataVo.setSqlType("QUERY");
                        } else {
                            log.error("flow模式不支持的引擎类型:{}", engineInfo.get("engine_type").toString());
                            throw new CustomException("flow模式不支持的引擎类型:" + engineInfo.get("engine_type").toString());
                        }
                    }
                } else {
                    log.error("查询不到引擎信息:execFlowSql:engineNo={}", doEngineDataVo.getEngineNo());
                    throw new CustomException("查询不到引擎信息:execFlowSql:engineNo=" + doEngineDataVo.getEngineNo());
                }
            }
            //按顺序执行sql
            try {
                for (DoEngineDataVo engineDataVo : doEngineDataVoList) {

                    String sql = engineDataVo.getSql();
                    try {
                        if ("SQL-EXEC".equals(engineDataVo.getSqlType())) {
                            Statement stmt = connection.createStatement();
                            rows += stmt.executeUpdate(sql);
                            stmt.close();
                        } else {
                            ResultSet resultSet = null;
                            PreparedStatement pstmt = null;
                            List<LinkedHashMap<String, Object>> resultList = new ArrayList<>();

                            String countSql = "select count(1) from (" + sql + ") a";
                            long totalCount = 0;
                            boolean hasPage = false;
                            final String limit = " limit";
                            if (connection == null) {
                                throw new CustomException("获取不到连接信息");
                            }
                            if (!sql.contains(limit) && engineDataVo.getQuerySqlParam() != null && engineDataVo.getQuerySqlParam().getIsPage() != null && engineDataVo.getQuerySqlParam().getIsPage()) {
                                hasPage = true;
                                preparedStatement = connection.prepareStatement(countSql);
                                ResultSet rs = preparedStatement.executeQuery();
                                while (rs.next()) {
                                    totalCount = rs.getLong(1);
                                }
                                sql += " limit " + (engineDataVo.getQuerySqlParam().getPageIndex() - 1) * engineDataVo.getQuerySqlParam().getPageSize() + "," + engineDataVo.getQuerySqlParam().getPageSize();
                                rs.close();
                                preparedStatement.close();
                            }
                            boolean hasData = !hasPage || (hasPage && totalCount > 0);
                            if (hasData) {
                                pstmt = connection.prepareStatement(sql);
                                resultSet = pstmt.executeQuery();

                                ResultSetMetaData metaData = resultSet.getMetaData();
                                int columnCount = metaData.getColumnCount();
                                while (resultSet.next()) {
                                    LinkedHashMap<String, Object> map = new LinkedHashMap<>(16);
                                    for (int i = 1; i <= columnCount; i++) {
                                        String label = metaData.getColumnLabel(i);
                                        map.put(label, resultSet.getString(i));
                                    }
                                    resultList.add(map);
                                }
                            }
                            if (!hasPage) {
                                totalCount = resultList.size();
                            }
                            resultSet.close();
                            pstmt.close();
                            res = new BasePageResult<>(resultList, totalCount);
                        }
                    } catch (Exception e) {
                        log.error("sql执行失败:sql:{}", sql);
                        throw new CustomException("sql执行失败:sql:" + sql);
                    }
                }
                connection.commit();
            } catch (CustomException e) {
                connection.rollback();
                throw e;
            } catch (Exception e) {
                connection.rollback();
                log.error("executeUpdateSql:异常sql：{}", doEngineDataVoList, e);
                throw new CustomException("sql流执行失败:" + doEngineDataVoList);
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("异常sql：{};参数:{}", doEngineDataVoList, busiData);
            throw new CustomException(e.getMessage());
        } finally {
            JDBCUtilsDruid.close(null, null, connection);
        }
        return res;
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
}
