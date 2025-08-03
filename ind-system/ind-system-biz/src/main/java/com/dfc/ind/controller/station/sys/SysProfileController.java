package com.dfc.ind.controller.station.sys;

import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.log.annotation.Log;
import com.dfc.ind.common.log.enums.BusinessType;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.sys.SysUser;
import com.dfc.ind.service.sys.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 个人信息 业务处理
 * 
 * @author admin
 */
@RestController
@RequestMapping("/user/profile")
public class SysProfileController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    /**
     * 个人信息
     */
    @GetMapping
    public JsonResults profile()
    {
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username);
        JsonResults ajax = JsonResults.success(user);
        ajax.put("roleGroup", userService.selectUserRoleGroup(username));
        ajax.put("postGroup", userService.selectUserPostGroup(username));
        return ajax;
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public JsonResults updateProfile(@RequestBody SysUser user)
    {
        if (userService.updateUserProfile(user) > 0)
        {
            return JsonResults.success();
        }
        return JsonResults.error("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public JsonResults updatePwd(String oldPassword, String newPassword)
    {
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username);
        String password = user.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password))
        {
            return JsonResults.error("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password))
        {
            return JsonResults.error("新密码不能与旧密码相同");
        }
        if (userService.resetUserPwd(username, SecurityUtils.encryptPassword(newPassword)) > 0)
        {
            return JsonResults.success();
        }
        return JsonResults.error("修改密码异常，请联系管理员");
    }
}
