package com.dfc.ind.mapper;

import com.dfc.ind.entity.LoadExcelInfoEntity;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;

/**
 * <p>
 * 导入模板信息 Mapper 接口
 * </p>
 *
 * @author huff
 * @since 2024-07-22
 */
public interface LoadExcelInfoMapper extends MppBaseMapper<LoadExcelInfoEntity> {

    void annualBusinessPlanFlash(Long merchantId, String importDate, String templateNo, String appId, String scoreName, String colId, String tableName);

    void convCustProdAlias(Long merchantId, String importDate, String templateNo);

    void convProdProdNo(Long merchantId, String importDate, String templateNo);
}
