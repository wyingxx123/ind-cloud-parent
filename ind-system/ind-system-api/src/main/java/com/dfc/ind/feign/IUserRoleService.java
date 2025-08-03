package com.dfc.ind.feign;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.feign.fallback.UserRoleServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 描述:
 * </p>
 *
 * @author wudj 伍达将
 * @date 2020/7/14
 * @copyright 武汉数慧享智能科技有限公司
 */
@FeignClient(value = "ind-system", fallback = UserRoleServiceFallBack.class)
public interface IUserRoleService {
    /**
     * 描述：获取用户岗位角色信息
     *
     * @param userId
     * @return com.dfc.ind.common.utils.JsonResults
     * @author wdj
     * @date 2020-07-14 15:12:39
     */
    @GetMapping("/user/userDetails/{userId}")
    public JsonResults getUserById(@PathVariable Long userId);

    /**
     * 描述：角色信息表 明细
     *
     * @param id
     * @return com.dfc.ind.common.utils.JsonResults
     * @author wdj
     * @date 2020-07-14 15:58:43
     */
    @GetMapping("/role/{id}")
    public JsonResults getRoleById(@PathVariable Long id);

    /**
     * 根据用户ID获取角色权限
     *
     * @param userId
     * @return
     */
    @GetMapping("/role/getRolesByUserId")
    public JsonResults getRolesByUserId(@RequestParam("userId") Long userId);

    /**
     * 根据角色类型获取当前商户下的角色编号
     *
     * @param roleTypes
     * @return
     */
    @GetMapping("/role/getRoleIdsByList")
    JsonResults getRoleIdsByList(@RequestParam(required = false) String roleTypes, @RequestParam(required = false) String roleIds);

    /**
     * 根据用户ID获取角色ID
     *
     * @param userId
     * @return
     */
    @GetMapping("/role/selectRoleIdsByUserId")
    JsonResults selectRoleIdsByUserId(@RequestParam("userId") Long userId);

    /**
     * 查询用户角色详细信息
     *
     * @param roleType
     * @param merchantId
     * @return
     */
    @GetMapping("/user/getUserRoleInfo")
    JsonResults getUserRoleInfo(@RequestParam(value = "roleType", required = false) String roleType, @RequestParam(value = "merchantId") Long merchantId);

    /**
     * 根据用户ID获取角色权限
     *
     * @param userId
     * @return
     */
    @GetMapping("/user/getGrade")
    String getGrade(@RequestParam String userName,@RequestParam Long merchantId);
}
