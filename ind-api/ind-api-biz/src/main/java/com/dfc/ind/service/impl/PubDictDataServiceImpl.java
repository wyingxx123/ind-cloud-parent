package com.dfc.ind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dfc.ind.utils.SecurityUtils;
import com.dfc.ind.entity.PubDictDataEntity;

import com.dfc.ind.mapper.PubDictDataMapper;
import com.dfc.ind.service.IPubDictDataService;
import com.dfc.ind.vo.PubDictDataVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商户参数值信息 服务实现类
 * </p>
 *
 * @author huff
 * @since 2023-05-17
 */
@Service
public class PubDictDataServiceImpl extends ServiceImpl<PubDictDataMapper, PubDictDataEntity> implements IPubDictDataService {

    @Override
    public List<PubDictDataVo> getDictDataByType(Long merchantId, String dictType, String dictCode) {

        return baseMapper.getDictDataByType(merchantId,dictType,dictCode);
    }

    @Override
    public String getMaxDictCodeByDictType(String dictType, Long merchantId) {
        return baseMapper.getMaxDictCodeByDictType(dictType,merchantId);
    }

    @Override
    public void addDictData(PubDictDataEntity pubDictDataEntity) {
        pubDictDataEntity.setOperator(SecurityUtils.getUserName());
        pubDictDataEntity.setOpTime(new Date());
        pubDictDataEntity.setOpDate(new Date());
        pubDictDataEntity.setStatus("00");
        pubDictDataEntity.setSortNo(1);
        this.saveOrUpdate(pubDictDataEntity);
    }

    @Override
    public List<PubDictDataVo> getDictDataByTypeLike(PubDictDataEntity entity) {
        entity.setMerchantId(SecurityUtils.getLoginUser().getMerchantId());
        return baseMapper.getDictDataByTypeLike(entity);
    }

    @Override
    public List<PubDictDataEntity> getDictDataByCodeName(Long merchantId, String dictType, String codeName) {

        QueryWrapper<PubDictDataEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(PubDictDataEntity::getMerchantId,merchantId)
                .eq(PubDictDataEntity::getDictType,dictType)
                .eq(PubDictDataEntity::getCodeName,codeName);
        return this.list(wrapper);

    }

    @Override
    public List<PubDictDataVo> getDictData(PubDictDataEntity entity) {

        return baseMapper.getDictData(entity);
    }
}
