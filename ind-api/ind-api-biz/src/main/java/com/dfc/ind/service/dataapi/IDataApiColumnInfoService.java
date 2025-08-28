package com.dfc.ind.service.dataapi;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.dataapi.DataApiColumnInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * api字段配置表 服务类
 * </p>
 *
 * @author huff
 * @since 2024-04-07
 */
public interface IDataApiColumnInfoService extends IService<DataApiColumnInfoEntity> {

    JsonResults syncColumnInfo(String appId);

    JsonResults pageList(Page startPage, DataApiColumnInfoEntity entity);
}
