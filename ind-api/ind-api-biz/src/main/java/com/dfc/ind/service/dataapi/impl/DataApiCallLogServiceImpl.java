package com.dfc.ind.service.dataapi.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfc.ind.entity.dataapi.DataApiCallLogEntity;
import com.dfc.ind.mapper.dataapi.DataApiCallLogMapper;
import com.dfc.ind.service.dataapi.IDataApiCallLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 数据服务调用日志 服务实现类
 * </p>
 *
 * @author huff
 * @since 2024-03-22
 */
@Service
public class DataApiCallLogServiceImpl extends ServiceImpl<DataApiCallLogMapper, DataApiCallLogEntity> implements IDataApiCallLogService {

}
