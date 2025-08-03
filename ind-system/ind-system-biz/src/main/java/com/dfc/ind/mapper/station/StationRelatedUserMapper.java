package com.dfc.ind.mapper.station;

import com.dfc.ind.entity.station.StationRelatedUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 描述:
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2020/9/2
 * @copyright 武汉数慧享智能科技有限公司
 */
public interface StationRelatedUserMapper extends BaseMapper<StationRelatedUserEntity> {

    public List<StationRelatedUserEntity> getByIds(List<String> idList);

    /**
     * 修改
     * @param entity
     * @return
     */
    int update(StationRelatedUserEntity entity);
}
