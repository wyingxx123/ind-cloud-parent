package com.dfc.ind.service.merchant;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.merchant.AgrMerchantInfoEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商户实体表 服务类
 * </p>
 *
 * @author nwy
 * @since 2020-03-26
 */
public interface IAgrMerchantInfoService extends IService<AgrMerchantInfoEntity> {
    /**
     * 根据用户id获取商户信息
     *
     * @param userId
     * @return
     */
    public AgrMerchantInfoEntity getMerchantInfo(Long userId);

    /**
     * <分页条件查询>
     *
     * @param startPage
     * @param entity          商户信息对象
     * @param filterCurrentId 过滤当前商户
     * @return
     * @author ylyan
     * @Date 2020/4/8 14:51
     */
    IPage pageList(Page startPage, AgrMerchantInfoEntity entity, Boolean filterCurrentId);

    /**
     * <系统管理员删除商户信息>
     *
     * @param merchantId 商户id
     * @return
     * @author ylyan
     * @Date 2020/4/27 17:30
     */
    boolean removeMerchantById(Long merchantId);

    JsonResults updateMerchantStatus(AgrMerchantInfoEntity entity);
}
