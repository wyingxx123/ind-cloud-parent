package com.dfc.ind.service.merchant;


import com.dfc.ind.entity.merchant.AgrFactoryInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 工厂信息表 服务类
 * </p>
 *
 * @author ylyan
 * @since 2020-04-13
 */
public interface IAgrFactoryInfoService extends IService<AgrFactoryInfoEntity> {

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
