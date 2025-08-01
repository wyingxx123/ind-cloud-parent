package com.dfc.ind.feign;

import com.dfc.ind.common.core.domain.R;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.sys.SysUser;
import com.dfc.ind.feign.fallback.RemoteUserServiceFallbackImpl;
import com.dfc.ind.sys.model.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 描述：用户远程调用接口
 *
 * @author admin
 */
@FeignClient(value = "ind-system", fallback = RemoteUserServiceFallbackImpl.class)
public interface RemoteUserService {

    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @return 结果
     */
    @GetMapping(value = "/user/info/{username}")
    public R<UserInfo> getUserInfo(@PathVariable("username") String username);

    /**
     * 更改用户状态
     *
     * @param user
     * @return
     */
    @PutMapping("/user/changeStatus")
    public JsonResults changeStatus(@RequestBody SysUser user);

    /**
     * 物理删除
     *
     * @param userId
     * @return
     */
    @DeleteMapping("/user/deleteRecord")
    public JsonResults deleteRecordById(@RequestParam Long userId);

    /**
     * 根据用户编号获取详细信息
     */
    @GetMapping(value = {"/user/{userId}"})
    public JsonResults getInfo(@PathVariable(value = "userId", required = false) Long userId);

    @GetMapping("/user/getUsersByRolesMerchantId")
    public JsonResults getUsersByRolesMerchantId(@RequestParam Long merchantId, @RequestParam String roleIds);

    /**
     * 根据用户编号获取详细信息
     */
    @GetMapping(value = {"/user/info/getUserByPhone"})
    public R<UserInfo> getUserByPhone(@RequestParam String phoneNumber);

    /**
     * 提供给微信授权添加openId，根据手机号码获取用户信息
     */
    @GetMapping(value = {"/user/info/getUserForPhone"})
    List<SysUser> getUserForPhone(@RequestParam String phoneNumber);

    /**
     * 提供给微信授权添加openId的接口
     * @param user
     * @return
     */
    @PutMapping("/user/info/editUserForPhone")
    JsonResults editUserForPhone(@RequestBody SysUser user);

    /**
     * 通过用户编号查询用户信息
     * @param userId
     * @return
     */
    @GetMapping("/user/info/getUserForId")
    SysUser getUserForId(@RequestParam Long userId);
    /**
     * 通过用户编号查询用户信息
     * @param userId
     * @return
     */
    @GetMapping("/user/list/{merchantId}")
    JsonResults mAndUList(@PathVariable Long merchantId);
    @GetMapping("/user/getUserListByRoleIds")
     List<SysUser> getUserListByRoleIds(@RequestParam String roleIds,@RequestParam Long merchantId);

    /**
     * 根据用户编号获取详细信息
     */
    @GetMapping(value = {"/user/info/getUserByPhoneAndMerchantId"})
     R<UserInfo> getUserByPhoneAndMerchantId(@RequestParam String phoneNumber,@RequestParam String merchantId);
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @return 结果
     */
    @GetMapping(value = "/user/info/getUserInfoAndMerchantId")
     R<UserInfo> getUserInfoAndMerchantId(@RequestParam String username,@RequestParam String merchantId);
}
