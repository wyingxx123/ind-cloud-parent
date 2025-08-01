package com.dfc.ind.feign.fallback;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.feign.IUserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 描述:
 * </p>
 *
 * @author wudj 伍达将
 * @date 2020/7/14
 * @copyright 武汉数慧享智能科技有限公司
 */
@Slf4j
@Component
public class UserRoleServiceFallBack implements IUserRoleService {

    @Override
    public JsonResults getUserById(Long userId) {
        log.error("获取用户岗位角色信息，用户id >> {}", userId);
        return JsonResults.error(500, "超时连接！");
    }

    @Override
    public JsonResults getRoleById(Long id) {
        log.error("获取角色信息，roleId >> {}", id);
        return JsonResults.error(500, "超时连接！");
    }

    @Override
    public JsonResults getRolesByUserId(Long userId) {
        log.error("获取用户角色信息，用户id >> {}", userId);
        return JsonResults.error(500, "超时连接！");
    }

    @Override
    public JsonResults getRoleIdsByList(String roleTypes, String roleIds) {
        log.error("获取角色信息，roles>> {}", roleTypes, roleIds);
        return JsonResults.error(500, "超时连接！");
    }

    @Override
    public JsonResults selectRoleIdsByUserId(Long userId) {
        log.error("获取角色信息，userId>> {}", userId);
        return JsonResults.error(500, "超时连接！");
    }

    @Override
    public JsonResults getUserRoleInfo(String roleType, Long merchantId) {
        log.error("获取用户角色信息，roleType>> {}", roleType);
        return JsonResults.error(500, "超时连接！");
    }

    @Override
    public String getGrade(String userName, Long merchantId) {
        return null;
    }
}
