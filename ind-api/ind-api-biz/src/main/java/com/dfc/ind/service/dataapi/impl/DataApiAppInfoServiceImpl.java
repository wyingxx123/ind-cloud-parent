package com.dfc.ind.service.dataapi.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfc.ind.entity.dataapi.DataApiAppInfoEntity;
import com.dfc.ind.mapper.dataapi.DataApiAppInfoMapper;
import com.dfc.ind.service.dataapi.IDataApiAppInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 数据应用系统信息 服务实现类
 * </p>
 *
 * @author huff
 * @since 2024-03-22
 */
@Service
public class DataApiAppInfoServiceImpl extends ServiceImpl<DataApiAppInfoMapper, DataApiAppInfoEntity> implements IDataApiAppInfoService {

}
