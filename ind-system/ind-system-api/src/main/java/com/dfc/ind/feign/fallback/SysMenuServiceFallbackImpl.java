package com.dfc.ind.feign.fallback;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.sys.SysMenu;
import com.dfc.ind.feign.ISysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 描述: 菜单feign接口
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2020/11/4
 * @copyright 武汉数慧享智能科技有限公司
 */
@Slf4j
@Component
public class SysMenuServiceFallbackImpl implements ISysMenuService {

    @Override
    public JsonResults add(SysMenu menu) {
        log.error("新增菜单失败，menuName >> {}", menu.getMenuName());
        return null;
    }
}
