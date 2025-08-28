package com.dfc.ind.service;

import com.dfc.ind.entity.PubDictDataEntity;

import com.dfc.ind.vo.PubDictDataVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商户参数值信息 服务类
 * </p>
 *
 * @author huff
 * @since 2023-05-17
 */
public interface IPubDictDataService extends IService<PubDictDataEntity> {
    List<PubDictDataVo> getDictDataByType(Long merchantId, String dictType, String dictCode);

    String getMaxDictCodeByDictType(String s, Long merchantId);

    void addDictData(PubDictDataEntity pubDictDataEntity);

    List<PubDictDataVo> getDictDataByTypeLike(PubDictDataEntity entity);

    List<PubDictDataEntity> getDictDataByCodeName(Long merchantId, String dictType, String codeName);

    List<PubDictDataVo> getDictData(PubDictDataEntity entity);
}
