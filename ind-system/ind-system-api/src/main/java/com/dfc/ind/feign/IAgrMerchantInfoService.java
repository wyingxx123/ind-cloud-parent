package com.dfc.ind.feign;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.feign.fallback.AgrMerchantInfoServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 商户实体表 服务调用类
 * </p>
 *
 * @author nwy
 * @since 2020-03-26
 */
@FeignClient(value = "ind-system", fallback = AgrMerchantInfoServiceFallBack.class)
public interface IAgrMerchantInfoService {

    /**
     * <商户实体表 明细>
     *
     * @param id 商户ID
     * @return
     * @author ylyan
     * @Date 2020/4/13 14:42
     */
    @GetMapping("/merchant/{id}")
    public JsonResults getById(@PathVariable Long id);

    /**
     * <根据serverId查询工厂信息>
     *
     * @param merchantId
     * @return
     * @author ylyan
     * @Date 2020/4/10 16:21
     */
    @GetMapping("/factory/info/{merchantId}")
    JsonResults info(@PathVariable Long merchantId);

    /**
     * <获取商户信息>
     *
     * @param filterCurrentId
     * @param merchantId
     * @param address
     * @param orderByColumn
     * @param pageNum
     * @param pageSize
     * @return
     * @author ylyan
     * @Date 2020/4/8 14:20
     */
    @GetMapping("/merchant/list")
    public JsonResults listPage(@RequestParam Boolean filterCurrentId, @RequestParam Long merchantId,
                                @RequestParam String address, @RequestParam String orderByColumn,
                                @RequestParam int pageNum, @RequestParam int pageSize);
}
