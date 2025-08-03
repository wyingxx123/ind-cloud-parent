package com.dfc.ind.service.dataapi.impl;


import com.dfc.ind.entity.dataapi.DataApiParaInfoEntity;
import com.dfc.ind.mapper.dataapi.DataApiParaInfoMapper;
import com.dfc.ind.service.dataapi.IDataApiParaInfoService;
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
public class DataApiParaInfoServiceImpl extends MppServiceImpl<DataApiParaInfoMapper, DataApiParaInfoEntity> implements IDataApiParaInfoService {

}
