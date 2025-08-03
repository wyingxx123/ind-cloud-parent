package com.dfc.ind.service.impl.merchant;


import com.dfc.ind.entity.merchant.AgrFactoryInfoEntity;
import com.dfc.ind.mapper.merchant.AgrFactoryInfoMapper;
import com.dfc.ind.service.merchant.IAgrFactoryInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 工厂信息表 服务实现类
 * </p>
 *
 * @author ylyan
 * @since 2020-04-13
 */
@Service
public class AgrFactoryInfoServiceImpl extends ServiceImpl<AgrFactoryInfoMapper, AgrFactoryInfoEntity> implements IAgrFactoryInfoService {

    @Override
    public AgrFactoryInfoEntity getByMerchantId(Long merchantId) {
        return baseMapper.getByMerchantId(merchantId);
    }
}
