package com.dfc.ind.service.impl.external;

import com.dfc.ind.entity.external.ExternalUserEntity;
import com.dfc.ind.mapper.external.ExternalUserMapper;
import com.dfc.ind.service.external.IExternalUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 描述: 内外部客户信息对照表 服务实现类
 * </p>
 *
 * @author wubt
 * @date 2020/11/26
 * @copyright 武汉数慧享智能科技有限公司
 */

@Service
public class ExternalUserServiceImpl extends ServiceImpl<ExternalUserMapper, ExternalUserEntity> implements IExternalUserService {

    @Override
    public ExternalUserEntity getUser(Long merchantId) {
        ExternalUserEntity externalUserEntity = baseMapper.selectOne(new QueryWrapper<ExternalUserEntity>().lambda()
                .eq(ExternalUserEntity::getMerchantId, merchantId)
                .eq(ExternalUserEntity::getDelFlag, "0"));
        return externalUserEntity;
    }

    @Override
    public IPage<ExternalUserEntity> pageList(Page startPage, ExternalUserEntity entity) {
        return page(startPage, queryWrapper(entity));
    }

    @Override
    public ExternalUserEntity getByExternalId(String externalId) {
        return this.getOne(new QueryWrapper<ExternalUserEntity>().lambda()
                .eq(ExternalUserEntity::getDelFlag, "0")
                .eq(ExternalUserEntity::getExternalId, externalId));
    }

    private QueryWrapper<ExternalUserEntity> queryWrapper(ExternalUserEntity entity) {
        QueryWrapper<ExternalUserEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ExternalUserEntity::getDelFlag, 0)
                .eq(null != entity.getMerchantId() && entity.getMerchantId() > 0, ExternalUserEntity::getMerchantId, entity.getMerchantId());
        return wrapper;
    }
}
