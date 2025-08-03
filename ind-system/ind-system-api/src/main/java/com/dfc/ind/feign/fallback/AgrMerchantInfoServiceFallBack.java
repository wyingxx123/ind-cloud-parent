package com.dfc.ind.feign.fallback;


import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.feign.IAgrMerchantInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <商户实体表 服务调用类>
 *
 * @author ylyan
 * @date 2020/1/14 11:51
 */
@Slf4j
@Component
public class AgrMerchantInfoServiceFallBack implements IAgrMerchantInfoService {

    @Override
    public JsonResults getById(Long id) {
        log.error("获取商户列表信息失败，id >> {}", id);
        return JsonResults.error(500, "超时连接！");
    }

    @Override
    public JsonResults info(Long merchantId) {
        return null;
    }

    @Override
    public JsonResults listPage(Boolean filterCurrentId, Long merchantId, String address, String orderByColumn, int pageNum, int pageSize) {
        return null;
    }
}
