package com.dfc.ind.service.impl.merchant;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.merchant.AgrMerchantInfoEntity;
import com.dfc.ind.mapper.merchant.AgrMerchantInfoMapper;
import com.dfc.ind.service.merchant.IAgrMerchantInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商户实体表 服务实现类
 * </p>
 *
 * @author nwy
 * @since 2020-03-26
 */
@Service
public class AgrMerchantInfoServiceImpl extends ServiceImpl<AgrMerchantInfoMapper, AgrMerchantInfoEntity> implements IAgrMerchantInfoService {


    @Autowired
    private AgrMerchantInfoMapper merchantInfoMapper;

    @Override
    public AgrMerchantInfoEntity getMerchantInfo(Long userId) {
        return baseMapper.getMerchantInfo(userId);
    }

    @Override
    public IPage pageList(Page startPage, AgrMerchantInfoEntity entity, Boolean filterCurrentId) {
        QueryWrapper<AgrMerchantInfoEntity> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .likeRight(!StringUtils.isEmpty(entity.getAddress()), AgrMerchantInfoEntity::getAddress, entity.getAddress())
                .likeRight(!StringUtils.isEmpty(entity.getMerchantName()), AgrMerchantInfoEntity::getMerchantName, entity.getMerchantName())
                .ge(null != entity.getBeginDate() && !"".equals(entity.getBeginDate()), AgrMerchantInfoEntity::getCreateTime, entity.getBeginDate())
                .le(null != entity.getEndDate() && !"".equals(entity.getEndDate()), AgrMerchantInfoEntity::getCreateTime, entity.getEndDate())
                .eq(StringUtils.isNotEmpty(entity.getApprovalStatus()), AgrMerchantInfoEntity::getApprovalStatus, entity.getApprovalStatus())
                .eq(StringUtils.isNotEmpty(entity.getStatus()), AgrMerchantInfoEntity::getStatus, entity.getStatus())
                .likeRight(null != entity.getMerchantId() && entity.getMerchantId() > 0, AgrMerchantInfoEntity::getMerchantId, entity.getMerchantId());
        return this.page(startPage, queryWrapper);
    }

    @Override
    public boolean removeMerchantById(Long merchantId) {
        //系统管理员删除商户信息,关联删除认证信息
        int removeId = baseMapper.removeMerchantById(merchantId);
        if (removeId > 0) {
            return true;
        }
        return false;
    }

    @Override
    public JsonResults updateMerchantStatus(AgrMerchantInfoEntity entity) {
        UpdateWrapper<AgrMerchantInfoEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("merchant_id", entity.getMerchantId())
                .set("status", entity.getStatus());
        if (StringUtils.isEmpty(entity.getStatus())) {
        return JsonResults.error();
        }
        return JsonResults.success(merchantInfoMapper.update(entity, updateWrapper));
    }
}
