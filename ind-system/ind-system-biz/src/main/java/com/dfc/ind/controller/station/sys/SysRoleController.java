package com.dfc.ind.controller.station.sys;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dfc.ind.common.core.constant.UserConstants;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.utils.poi.ExcelUtil;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.log.annotation.Log;
import com.dfc.ind.common.log.enums.BusinessType;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.sys.SysRole;
import com.dfc.ind.service.sys.ISysRoleService;
import com.dfc.ind.service.sys.ISysUserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 角色信息
 *
 * @author admin
 */
@RestController
@RequestMapping("/role")
public class SysRoleController extends BaseController {
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysUserService userService;

    //@PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public JsonResults list(SysRole role) {
        return JsonResults.success(roleService.pageList(startPage(), role));
    }

    /**
     * 角色信息表 分页查询
     *
     * @param role 对象
     */
    @GetMapping
    @ApiOperation(value = "查询全部")
    public JsonResults listAll(SysRole role) {
        return JsonResults.success(roleService.listAll(role));
    }

    /**
     * 角色信息表 分页查询
     *
     * @param role 对象
     */
    @PostMapping("/listAll")
    @ApiOperation(value = "查询全部")
    public JsonResults postListAll(@RequestBody SysRole role) {
        return roleService.listAll(role);
    }

    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:role:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRole role) throws Exception {
        List<SysRole> list = roleService.list(new QueryWrapper<SysRole>().setEntity(role));
        ExcelUtil<SysRole> util = new ExcelUtil<SysRole>(SysRole.class);
        util.exportExcel(response, list, "角色数据");
    }

    /**
     * 根据角色编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public JsonResults getInfo(@PathVariable Long roleId) {
        return JsonResults.success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public JsonResults add(@Validated @RequestBody SysRole role) {
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return JsonResults.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return JsonResults.error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        String userName = SecurityUtils.getUsername();
        role.setMerchantId(userService.selectUserByUserName(userName).getMerchantId());
        role.setCreateBy(userName);
        return toAjax(roleService.insertRole(role));

    }

    /**
     * 修改保存角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public JsonResults edit(@Validated @RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return JsonResults.error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return JsonResults.error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        int usercount =   roleService.countUserRoleByRoleId(role.getRoleId());
        if(usercount>0 && role.getStatus().equals("1")){
            return JsonResults.error("修改角色信息'"+ "'失败，当前角色正在使用")  ;
        }
        role.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(roleService.updateRole(role));
    }

    /**
     * 修改保存数据权限
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public JsonResults dataScope(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        return toAjax(roleService.authDataScope(role));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public JsonResults changeStatus(@RequestBody SysRole role) {
     int usercount =   roleService.countUserRoleByRoleId(role.getRoleId());
     if(usercount==0){
         roleService.checkRoleAllowed(role);
         role.setUpdateBy(SecurityUtils.getUsername());
         roleService.updateRoleStatus(role);
     }else{
         return JsonResults.error("修改状态'"+ "'失败，当前角色正在使用");
     }
        return toAjax(1);
    }

    /**
     * 删除角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public JsonResults remove(@PathVariable Long[] roleIds) {
        return toAjax(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 获取角色选择框列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/optionselect")
    public JsonResults optionselect() {
        return JsonResults.success(roleService.selectRoleAll());
    }

    /**
     * 获取二维码信息
     *
     * @param roleId 角色编号
     */
    @GetMapping("/twoCode")
    @ApiOperation(value = "获取二维码信息")
    @ApiImplicitParam(name = "roleId", value = "角色ID", required = true, dataType = "Long")
    public JsonResults twoCode(Long roleId) {
        SysRole entity = roleService.getById(roleId);
        if (StringUtils.isNull(entity)) {
            return JsonResults.error();
        }
        return roleService.twoCode(entity);
    }

    /**
     * 根据角色类型等级获取所有用户id
     *
     * @param roleType 角色类型等级
     * @return
     */
    @GetMapping("/getUserId")
    @ApiOperation(value = "根据角色类型等级获取所有用户id")
    @ApiImplicitParam(name = "roleType", value = "角色类型等级", required = true, dataType = "String")
    public JsonResults getUserId(String roleType) {
        return JsonResults.success(roleService.getUserId(roleType));
    }


    /**
     * 根据角色类型等级获取所有用户id,商户id
     *
     * @param roleType 角色类型等级
     * @return
     */
    @GetMapping("/getUserMerchant")
    @ApiOperation(value = "根据角色类型等级获取所有用户id,商户id")
    @ApiImplicitParam(name = "roleType", value = "角色类型等级", required = true, dataType = "String")
    public JsonResults getUserMerchant(String roleType) {
        return JsonResults.success(roleService.getUserMerchant(roleType));
    }

    /**
     * 根据用户ID获取角色权限
     *
     * @param userId
     * @return
     */
    @GetMapping("/getRolesByUserId")
    public JsonResults getRolesByUserId(@RequestParam("userId") Long userId) {
        return JsonResults.success(roleService.selectRolePermissionByUserId(userId));
    }

    /**
     * 根据用户ID获取角色ID
     *
     * @param userId
     * @return
     */
    @GetMapping("/selectRoleIdsByUserId")
    public JsonResults selectRoleIdsByUserId(@RequestParam("userId") Long userId) {
        return JsonResults.success(roleService.selectRoleIdsByUserId(userId));
    }

    /**
     * 根据角色类型获取当前商户下的角色编号
     *
     * @param roleTypes
     * @return
     */
    @GetMapping("/getRoleIdsByList")
    public JsonResults getRoleIdsByList(@RequestParam(required = false) String roleTypes, @RequestParam(required = false) String roleIds) {
        return roleService.getRoleIdsByList(roleTypes, roleIds);
    }
}
