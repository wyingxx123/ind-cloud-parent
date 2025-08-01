package com.dfc.ind.feign.fallback;

import com.dfc.ind.common.core.domain.R;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.sys.SysUser;
import com.dfc.ind.feign.RemoteUserService;
import com.dfc.ind.sys.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author admin
 */
@Slf4j
@Component
public class RemoteUserServiceFallbackImpl implements RemoteUserService {


    @Override
    public R<UserInfo> getUserInfo(String username) {
        log.error("查询用户信息失败，username >> {}", username);
        return null;
    }

    @Override
    public JsonResults changeStatus(SysUser user) {
        log.error("修改用户信息失败，userId >> {}", user.getUserId());
        return JsonResults.error("fallback");
    }

    @Override
    public JsonResults deleteRecordById(Long userId) {
        log.error("删除用户信息失败，userId >> {}", userId);
        return JsonResults.error("fallback");
    }

    @Override
    public JsonResults getInfo(Long userId) {
        log.error("查询用户信息失败，userId >> {}", userId);
        return JsonResults.error("fallback");
    }

    @Override
    public JsonResults getUsersByRolesMerchantId(Long merchantId, String roleTypes) {
        log.error("查询用户信息失败，rolesType >> {}", roleTypes);
        return JsonResults.error("fallback");
    }

    @Override
    public R<UserInfo> getUserByPhone(String phoneNumber) {
        log.error("查询用户信息失败，phoneNumber >> {}", phoneNumber);
        return null;
    }

    @Override
    public List<SysUser> getUserForPhone(@RequestParam String phoneNumber) {
        log.error("查询用户信息失败，phoneNumber >> {}", phoneNumber);
        return null;
    }

    @Override
    public JsonResults editUserForPhone(@RequestBody SysUser user) {
        log.error("修改用户信息失败，userId >> {}", user.getUserId());
        return null;
    }
    @Override
    public SysUser getUserForId(@RequestParam Long userId) {
        log.error("修改用户信息失败，userId >> {}", userId);
        return null;
    }

    @Override
    public JsonResults mAndUList(Long merchantId) {
        return null;
    }

    @Override
    public List<SysUser> getUserListByRoleIds(String roleIds, Long merchantId) {
        return null;
    }

    @Override
    public R<UserInfo> getUserByPhoneAndMerchantId(String phoneNumber, String merchantId) {
        log.error("查询用户信息失败，phoneNumber >> {}", phoneNumber);
        return null;
    }

    @Override
    public R<UserInfo> getUserInfoAndMerchantId(String username, String merchantId) {
        log.error("查询用户信息失败，username >> {}", username);
        return null;
    }
}
