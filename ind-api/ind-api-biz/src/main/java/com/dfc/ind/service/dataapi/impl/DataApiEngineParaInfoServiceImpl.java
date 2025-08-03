package com.dfc.ind.service.dataapi.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.dfc.ind.entity.dataapi.DataApiEngineParaInfoEntity;
import com.dfc.ind.mapper.dataapi.DataApiEngineParaInfoMapper;
import com.dfc.ind.service.dataapi.IDataApiEngineParaInfoService;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author huff
 * @since 2022-09-08
 */
@Service
public class DataApiEngineParaInfoServiceImpl extends MppServiceImpl<DataApiEngineParaInfoMapper, DataApiEngineParaInfoEntity> implements IDataApiEngineParaInfoService {

    @Override
    public List<DataApiEngineParaInfoEntity> getByEngineNo(String engineNO) {
        QueryWrapper<DataApiEngineParaInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DataApiEngineParaInfoEntity::getEngineNo,engineNO).orderByAsc(DataApiEngineParaInfoEntity::getEngineParaNo);
        return baseMapper.selectList(queryWrapper);
    }
}
