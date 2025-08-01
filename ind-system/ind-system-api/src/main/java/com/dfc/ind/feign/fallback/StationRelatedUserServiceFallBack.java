package com.dfc.ind.feign.fallback;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.station.StationRelatedUserEntity;
import com.dfc.ind.feign.IStationRelatedUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 描述:
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2020/9/4
 * @copyright 武汉数慧享智能科技有限公司
 */
@Slf4j
@Component
public class StationRelatedUserServiceFallBack implements IStationRelatedUserService {
    @Override
    public JsonResults add(StationRelatedUserEntity entity) {
        return null;
    }

    @Override
    public JsonResults update(StationRelatedUserEntity entity) {
        return null;
    }

    @Override
    public JsonResults deleteByStationNo(String stationNo) {
        return null;
    }

    @Override
    public JsonResults getOnyByStationNo(String stationNo) {
        return null;
    }

    @Override
    public JsonResults batchDeleteByStationNos(String stationNos) {
        return null;
    }

    @Override
    public JsonResults getByIds(String stationNos) {
        return null;
    }
    
    @Override
    public JsonResults createCodeNew(String codeNo, String codeType) {
        return null;
    }
    
    @Override
    public JsonResults codeAccess(String codeUrl, Long userId, String merchantId) {
        return null;
    }
    
}
