package com.dfc.ind.mapper;

import com.dfc.ind.entity.PubDictDataEntity;

import com.dfc.ind.vo.PubDictDataVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 商户参数值信息 Mapper 接口
 * </p>
 *
 * @author huff
 * @since 2023-05-17
 */
public interface PubDictDataMapper extends BaseMapper<PubDictDataEntity> {

    List<PubDictDataVo> getDictDataByType(Long merchantId, String dictType, String dictCode);

    List<PubDictDataVo> selectDictTypeAll();

    String getMaxDictCodeByDictType(String dictType, Long merchantId);

    /**
     * 根据字典类型模糊查询
     * @param entity
     * @return
     */
    List<PubDictDataVo> getDictDataByTypeLike(PubDictDataEntity entity);

    List<PubDictDataVo> getDictData(PubDictDataEntity entity);

}
