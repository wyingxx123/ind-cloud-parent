package com.dfc.ind.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.station.StationRelatedUserEntity;
import com.dfc.ind.feign.fallback.StationRelatedUserServiceFallBack;

/**
 * <p>
 * 描述:
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2020/9/4
 * @copyright 武汉数慧享智能科技有限公司
 */
@FeignClient(value = "ind-system", fallback = StationRelatedUserServiceFallBack.class)
public interface IStationRelatedUserService {

    @PutMapping("/stationRelatedUser")
    JsonResults add(@RequestBody StationRelatedUserEntity entity);

    @PostMapping("/stationRelatedUser")
    JsonResults update(@RequestBody StationRelatedUserEntity entity);

    @DeleteMapping("/stationRelatedUser")
    JsonResults deleteByStationNo(@RequestParam String stationNo);

    @GetMapping("/stationRelatedUser/{stationNo}")
    JsonResults getOnyByStationNo(@RequestParam String stationNo);

    @DeleteMapping("/stationRelatedUser/batchDelete")
    JsonResults batchDeleteByStationNos(@RequestParam String stationNos);

    @GetMapping("/stationRelatedUser/getByIds")
    JsonResults getByIds(@RequestParam String stationNos);
    
    @PutMapping("/stationRelatedUser/createCodeNew")
    JsonResults createCodeNew(@RequestParam String codeNo, @RequestParam String codeType);
    
    @GetMapping("/stationRelatedUser/codeAccess")
    JsonResults codeAccess(@RequestParam String codeUrl, @RequestParam Long userId, @RequestParam String merchantId);
}
