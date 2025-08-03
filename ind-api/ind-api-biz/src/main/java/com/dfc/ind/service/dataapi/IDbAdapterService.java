package com.dfc.ind.service.dataapi;

import com.dfc.ind.entity.dataapi.vo.DoEngineDataVo;
import com.dfc.ind.uitls.BasePageResult;

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
public interface IDbAdapterService  {


    BasePageResult execSqlQuery(String sqlCountStr, Map<String, Object> busiData) throws Exception;

    int execSqlExec(String sqlStr, Map<String, Object> busiData);


    Object execSqlFlows(List<DoEngineDataVo> doEngineDataVoList, Map<String, Object> busiData);
}
