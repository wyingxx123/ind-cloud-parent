package com.dfc.ind.service.impl.malluser;

import com.dfc.ind.entity.malluser.MallUserAddressEntity;
import com.dfc.ind.mapper.malluser.MallUserAddressMapper;
import com.dfc.ind.service.malluser.IMallUserAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * <p>
 * 用户地址表 服务实现类
 * </p>
 *
 * @author ylyan
 * @since 2020-04-23
 */
@Service
public class MallUserAddressServiceImpl extends ServiceImpl<MallUserAddressMapper, MallUserAddressEntity> implements IMallUserAddressService {

    @Override
    public boolean saveBatch(Collection<MallUserAddressEntity> entityList) {
        return false;
    }
}
