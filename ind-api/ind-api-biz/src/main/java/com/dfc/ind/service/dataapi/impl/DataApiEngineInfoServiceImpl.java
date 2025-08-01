package com.dfc.ind.service.dataapi.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfc.ind.entity.dataapi.DataApiEngineInfoEntity;
import com.dfc.ind.mapper.dataapi.DataApiEngineInfoMapper;
import com.dfc.ind.service.dataapi.IDataApiEngineInfoService;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author huff
 * @since 2022-09-08
 */
@Service
public class DataApiEngineInfoServiceImpl extends MppServiceImpl<DataApiEngineInfoMapper, DataApiEngineInfoEntity> implements IDataApiEngineInfoService {


}
