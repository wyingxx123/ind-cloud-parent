package com.dfc.ind.mapper.merchant;

import com.dfc.ind.entity.merchant.AgrFactoryInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 工厂信息表 Mapper 接口
 * </p>
 *
 * @author ylyan
 * @since 2020-03-28
 */
public interface AgrFactoryInfoMapper extends BaseMapper<AgrFactoryInfoEntity> {

    /**
     * <根据商户id获取工厂信息明细>
     *
     * @param merchantId 商户id
     * @return
     * @author ylyan
     * @Date 2020/4/13 12:58
     */
    AgrFactoryInfoEntity getByMerchantId(Long merchantId);
}
