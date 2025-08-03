package com.dfc.ind.mapper.dataapi;

import com.dfc.ind.entity.dataapi.DataApiInfoEntity;
import com.dfc.ind.entity.dataapi.param.LoadApiDataToRedisParam;
import com.dfc.ind.entity.dataapi.vo.ApiInfoCatchDTO;
import com.dfc.ind.entity.dataapi.vo.FiledTypeVo;
import com.dfc.ind.entity.dataapi.vo.MetaDataVo;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author huff
 * @since 2022-09-08
 */
public interface DataApiInfoMapper extends MppBaseMapper<DataApiInfoEntity> {
    /**
     * 获取表元数据 字段名,是否主键,字段顺序
     * param
     * param
     * @return
     */
    List<MetaDataVo> getMetaDataByTableName(LoadApiDataToRedisParam param);

    /**
     * 获取表数据
     * @param param
     * @return
     */
    List<Map<String, Object>> getData(LoadApiDataToRedisParam param);

    List<Map<String, Object>> getSourceData(String tableName);

    List<ApiInfoCatchDTO> getApiDataByAppId(String applicationCode);

    List<FiledTypeVo> getFiledTypeData();

    List<LinkedHashMap> getMetaData(LoadApiDataToRedisParam param);
}
