package com.dfc.ind.mapper.merchant;


import com.dfc.ind.entity.merchant.AgrMerchantInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 商户实体表 Mapper 接口
 * </p>
 *
 * @author nwy
 * @since 2020-03-26
 */
public interface AgrMerchantInfoMapper extends BaseMapper<AgrMerchantInfoEntity> {

    /**
     * 根据用户id获取商户信息
     *
     * @param userId
     * @return
     */
    public AgrMerchantInfoEntity getMerchantInfo(Long userId);

    /**
     * <系统管理员删除商户信息>
     *
     * @param merchantId 商户id
     * @return
     * @author ylyan
     * @Date 2020/4/27 17:30
     */
    int removeMerchantById(Long merchantId);
}
