package com.dfc.ind.service.dataapi;

import com.dfc.ind.entity.dataapi.DataApiEngineParaInfoEntity;
import com.github.jeffreyning.mybatisplus.service.IMppService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author huff
 * @since 2022-09-08
 */
public interface IDataApiEngineParaInfoService extends IMppService<DataApiEngineParaInfoEntity> {

    List<DataApiEngineParaInfoEntity> getByEngineNo(String engineNO);
}
