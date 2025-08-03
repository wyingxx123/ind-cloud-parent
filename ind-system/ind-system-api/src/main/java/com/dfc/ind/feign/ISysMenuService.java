package com.dfc.ind.feign;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.sys.SysMenu;
import com.dfc.ind.feign.fallback.SysMenuServiceFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 描述: 菜单feign接口
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2020/11/4
 * @copyright 武汉数慧享智能科技有限公司
 */
@FeignClient(value = "ind-system", fallback = SysMenuServiceFallbackImpl.class)
public interface ISysMenuService {

    @PostMapping("/menu")
    JsonResults add(@Validated @RequestBody SysMenu menu);

}
