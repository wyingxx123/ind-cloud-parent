package com.dfc.ind.service.dataapi.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfc.ind.entity.dataapi.DataSourceInfoEntity;
import com.dfc.ind.mapper.dataapi.DataSourceInfoMapper;
import com.dfc.ind.service.dataapi.IDataSourceInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 数据源注册信息 服务实现类
 * </p>
 *
 * @author huff
 * @since 2024-03-22
 */
@Service
public class DataSourceInfoServiceImpl extends ServiceImpl<DataSourceInfoMapper, DataSourceInfoEntity> implements IDataSourceInfoService {

}
