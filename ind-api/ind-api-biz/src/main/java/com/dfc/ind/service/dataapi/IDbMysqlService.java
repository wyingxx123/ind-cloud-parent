package com.dfc.ind.service.dataapi;

import com.dfc.ind.entity.dataapi.param.ExecuteQuerySqlParam;
import com.dfc.ind.entity.dataapi.vo.DoEngineDataVo;
import com.dfc.ind.uitls.BasePageResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author huff
 * @since 2022-09-08
 */
public interface IDbMysqlService {



    int execUpdate(String sqlStr, String data_source_id);
     BasePageResult<LinkedHashMap<String, Object>> executeQuerySql(ExecuteQuerySqlParam param) throws Exception;

    Object execFlowSql(List<DoEngineDataVo> doEngineDataVoList, String sourceKey, Map<String, Object> busiData);
}
