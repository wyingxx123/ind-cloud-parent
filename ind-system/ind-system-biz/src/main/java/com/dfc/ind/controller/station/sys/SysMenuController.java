package com.dfc.ind.controller.station.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dfc.ind.common.core.constant.Constants;
import com.dfc.ind.common.core.constant.UserConstants;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.log.annotation.Log;
import com.dfc.ind.common.log.enums.BusinessType;
import com.dfc.ind.common.security.domain.LoginUser;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.sys.SysMenu;
import com.dfc.ind.entity.vo.SyncInfoVo;
import com.dfc.ind.service.sys.ISysMenuService;
import com.dfc.ind.service.sys.ISysUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单信息
 *
 * @author admin
 */
@RestController
@RequestMapping("/menu")
public class SysMenuController extends BaseController
{
    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private ISysUserService userService;

    /**
     * 同步菜单信息
     *
     * @param menuId
     * @return
     */
    @ApiOperation(value = "同步菜单信息")
    @GetMapping("/getSynchronizeInfo/{menuId}")
    public JsonResults getSynchronizeInfo(@PathVariable Long menuId){
        return menuService.getSynchronizeInfo(menuId);
    }

    /**
     * 同步菜单信息,可以同步到所有商户，也可以同步到指定商户
     *
     * @param syncInfoVo
     * @return
     */
    @ApiOperation(value = "同步菜单信息")
    @PostMapping("/synchronizeMenu")
    public JsonResults synchronizeMenuInfo(@RequestBody SyncInfoVo syncInfoVo){
        return menuService.synchronizeMenuInfo(syncInfoVo);
    }

    /**
     * 获取菜单列表
     */
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public JsonResults list(SysMenu menu)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        Long merchantId = loginUser.getMerchantId();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId, merchantId);
        return JsonResults.success(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public JsonResults getInfo(@PathVariable Long menuId)
    {
        return JsonResults.success(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public JsonResults treeselect(SysMenu menu)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        Long merchantId = loginUser.getMerchantId();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId, merchantId);
        return JsonResults.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public JsonResults roleMenuTreeselect(@PathVariable("roleId") Long roleId)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long merchantId = loginUser.getMerchantId();
        Long userId = loginUser.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(userId, merchantId);
        JsonResults ajax = JsonResults.success();
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        ajax.put("menus", menuService.buildMenuTreeSelect(menus));
        return ajax;
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Log(title = "菜单管理-新增", businessType = BusinessType.INSERT)
    @PostMapping
    public JsonResults add(@Validated @RequestBody SysMenu menu)
    {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu)))
        {
            return JsonResults.error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame())
                && !StringUtils.startsWithAny(menu.getPath(), Constants.HTTP, Constants.HTTPS))
        {
            return JsonResults.error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }

        String userName=SecurityUtils.getUsername();
        menu.setCreateBy(SecurityUtils.getUserName());
        menu.setCreateTime(DateUtils.getNowDate());
        Long merchantId = null != SecurityUtils.getLoginUser().getMerchantId() ? SecurityUtils.getLoginUser().getMerchantId() : 0L;
        menu.setMerchantId(merchantId);
        return menuService.save(menu)?JsonResults.success("新增成功"):JsonResults.error("新增失败");
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理-修改", businessType = BusinessType.UPDATE)
    @PutMapping
    public JsonResults edit(@Validated @RequestBody SysMenu menu)
    {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu)))
        {
            return JsonResults.error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame())
                && !StringUtils.startsWithAny(menu.getPath(), Constants.HTTP, Constants.HTTPS))
        {
            return JsonResults.error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        String userName=SecurityUtils.getUsername();
        menu.setMerchantId(userService.selectUserByUserName(userName).getMerchantId());
        menu.setUpdateBy(userName);
        return menuService.updateMenuById(menu)?JsonResults.success("修改成功"):JsonResults.error("修改失败");
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @Log(title = "菜单管理-删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public JsonResults remove(@PathVariable("menuId") Long menuId)
    {
        LambdaQueryWrapper<SysMenu> queryWrapper = new QueryWrapper<SysMenu>().lambda().eq(SysMenu::getMenuId, menuId).eq(SysMenu::getMerchantId, SecurityUtils.getLoginUser().getMerchantId());

        if (menuService.hasChildByMenuId(menuId))
        {
            return JsonResults.error("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return JsonResults.error("菜单已分配,不允许删除");
        }
        return menuService.remove(queryWrapper)?JsonResults.success("删除成功"):JsonResults.error("删除失败");
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public JsonResults getRouters()
    {
        Long userId = SecurityUtils.getLoginUser().getUserId();
        Long merchantId=SecurityUtils.getLoginUser().getMerchantId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId,merchantId);
        return JsonResults.success(menuService.buildMenus(menus));
    }
}
