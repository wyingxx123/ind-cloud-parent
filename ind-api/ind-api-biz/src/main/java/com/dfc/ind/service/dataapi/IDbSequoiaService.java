package com.dfc.ind.service.dataapi;

import com.dfc.ind.entity.dataapi.param.ExecuteQuerySqlParam;
import com.dfc.ind.uitls.BasePageResult;

import java.util.LinkedHashMap;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author huff
 * @since 2024-03-04
 */
public interface IDbSequoiaService {
     BasePageResult<LinkedHashMap<String, Object>> executeQuery(ExecuteQuerySqlParam param) throws Exception;
    int execUpdate(String sqlStr, ExecuteQuerySqlParam param);

    int execInsert(String sqlStr, ExecuteQuerySqlParam param);
}
