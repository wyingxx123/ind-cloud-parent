package com.dfc.ind.feign;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.feign.fallback.ExternalUserServiceFallBackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 描述:
 * </p>
 *
 * @author wubt
 * @date 2020/11/26
 * @copyright 武汉数慧享智能科技有限公司
 */
@FeignClient(value = "ind-system", fallback = ExternalUserServiceFallBackImpl.class)
public interface IExternalUserService {
    /**
     * 描述：根据商户Id拿到用户
     *
     * @param merchantId
     * @return com.dfc.ind.common.core.web.domain.JsonResults
     * @author wubt
     * @date 2020-11-26
     */
    @GetMapping("/externalUser")
    JsonResults getExternalUser(@RequestParam Long merchantId);

    @GetMapping("externalUser/getMerchantId")
    JsonResults getMerchantId(@RequestParam String externalId);
}
