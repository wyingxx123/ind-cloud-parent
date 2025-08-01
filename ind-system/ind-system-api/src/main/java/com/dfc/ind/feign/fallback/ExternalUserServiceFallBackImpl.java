package com.dfc.ind.feign.fallback;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.feign.IExternalUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 描述:
 * </p>
 *
 * @author wubt
 * @date 2020/11/26
 * @copyright 武汉数慧享智能科技有限公司
 */
@Slf4j
@Component
public class ExternalUserServiceFallBackImpl implements IExternalUserService {
    @Override
    public JsonResults getExternalUser(Long merchantId) {
        log.error("获取用户信息，用户id >> {}", merchantId);
        return JsonResults.error(500, "超时连接！");
    }

    @Override
    public JsonResults getMerchantId(String externalId) {
        log.error("获取商户信息，用户id >> {}", externalId);
        return JsonResults.error(500, "获取商户信息失败");
    }
}
