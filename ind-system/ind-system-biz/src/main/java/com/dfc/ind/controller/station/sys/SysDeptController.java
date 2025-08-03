package com.dfc.ind.controller.station.sys;

import com.dfc.ind.common.core.constant.UserConstants;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.log.annotation.Log;
import com.dfc.ind.common.log.enums.BusinessType;
import com.dfc.ind.common.security.domain.LoginUser;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.sys.SysDept;
import com.dfc.ind.entity.sys.SysUser;
import com.dfc.ind.service.sys.ISysDeptService;
import com.dfc.ind.service.sys.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dfc.ind.utils.CryptoUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

/**
 * 部门信息
 *
 * @author admin
 */
@RestController
@RequestMapping("/dept")
public class SysDeptController extends BaseController
{
    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysUserService userService;

    /**
     * 获取部门列表
     */
    //@PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    public JsonResults list(SysDept entity)
    {
        entity.setMerchantId(SecurityUtils.getLoginUser().getMerchantId());
        QueryWrapper<SysDept> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .likeRight(!StringUtils.isEmpty(entity.getDeptName()), SysDept::getDeptName, entity.getDeptName())
                .eq(!StringUtils.isEmpty(entity.getStatus()), SysDept::getStatus, entity.getStatus())
                .orderByAsc(SysDept::getOrderNum)
                .eq(null != entity.getMerchantId() && entity.getMerchantId() > 0, SysDept::getMerchantId, entity.getMerchantId())
                .eq(SysDept::getDelFlag, "0");
        return JsonResults.success(deptService.page(startPage(), queryWrapper));
    }

    /**
     * 查询部门列表（排除节点）
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list/exclude/{deptId}")
    public JsonResults excludeChild(@PathVariable(value = "deptId", required = false) Long deptId)
    {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        Iterator<SysDept> it = depts.iterator();
        while (it.hasNext())
        {
            SysDept d = (SysDept) it.next();
            if (d.getDeptId().intValue() == deptId
                    || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""))
            {
                it.remove();
            }
        }
        return JsonResults.success(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
   // @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @SneakyThrows
    @GetMapping(value = {"/", "/{deptId}"})
    public JsonResults getInfo(@PathVariable(value = "deptId", required = false) Long deptId)
    {
        JsonResults results = JsonResults.success();

        List<SysUser> userList = userService.list(new QueryWrapper<SysUser>()
                .lambda()
                .eq(SysUser::getDelFlag, "0")
                .eq(SysUser::getUserId, SecurityUtils.getLoginUser().getUserId())
        );

        //==============解决渗透安全  敏感信息字段进行加密
        for (SysUser sysUser : userList) {
            //手机号加密
            if(StringUtils.isNotEmpty(sysUser.getPhonenumber())){
                sysUser.setPhonenumber(CryptoUtils.encrypt(sysUser.getPhonenumber()));
            }

            //邮箱加密
            if(StringUtils.isNotEmpty(sysUser.getEmail())){
                sysUser.setEmail(CryptoUtils.encrypt(sysUser.getEmail()));
            }
        }
        results.put("userList", userList);
        SysDept dept=null;
        if (StringUtils.isNotNull(deptId)) {
             dept = deptService.getById(deptId);
            //==============解决渗透安全 用户权限A用户不能访问到B用户的数据
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser sysUser = userService.selectUserTypeById (loginUser.getUserId());
            //手机号加密
            if(StringUtils.isNotEmpty(dept.getPhone())){
                dept.setPhone(CryptoUtils.encrypt(dept.getPhone()));
            }
            //邮箱加密
            if(StringUtils.isNotEmpty(dept.getEmail())){
                dept.setEmail(CryptoUtils.encrypt(dept.getEmail()));
            }
           if(sysUser.getUserType().equals("01")){
                results.put("data", dept);
           }

            return results;
        }
        return results;
    }

    /**
     * 获取部门下拉树列表
     */
    @GetMapping("/treeselect")
    public JsonResults treeselect(SysDept dept)
    {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return JsonResults.success(deptService.buildDeptTreeSelect(depts));
    }

    /**
     * 加载对应角色部门列表树
     */
    @GetMapping(value = "/roleDeptTreeselect/{roleId}")
    public JsonResults roleDeptTreeselect(@PathVariable("roleId") Long roleId)
    {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        JsonResults ajax = JsonResults.success();
        ajax.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        ajax.put("depts", deptService.buildDeptTreeSelect(depts));
        return ajax;
    }

    /**
     * 新增部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public JsonResults add(@Validated @RequestBody SysDept dept)
    {
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept)))
        {
            return JsonResults.error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        dept.setCreateBy(SecurityUtils.getUsername());
        dept.setCreateTime(DateUtils.getNowDate());
        dept.setMerchantId(SecurityUtils.getLoginUser().getMerchantId());
        deptService.insertDept(dept);
        return JsonResults.success();
    }

    /**
     * 修改部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public JsonResults edit(@Validated @RequestBody SysDept dept)
    {
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept)))
        {
            return JsonResults.error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        else if (dept.getParentId().equals(dept.getDeptId()))
        {
            return JsonResults.error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        }
        else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus())
                && deptService.selectNormalChildrenDeptById(dept.getDeptId()) > 0)
        {
            return JsonResults.error("该部门包含未停用的子部门！");
        }
        dept.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 删除部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public JsonResults remove(@PathVariable Long deptId)
    {
        if (deptService.hasChildByDeptId(deptId))
        {
            return JsonResults.error("存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId))
        {
            return JsonResults.error("部门存在用户,不允许删除");
        }
        return toAjax(deptService.deleteDeptById(deptId));
    }

    /**
     * 部门表 返回树状数据
     */
    @GetMapping("/treeData")
    public JsonResults treeData(SysDept dept) {
        Long merchantId =  SecurityUtils.getLoginUser().getMerchantId();
        List<SysDept> deptList = deptService.list(new QueryWrapper<SysDept>().lambda()
                        .eq(SysDept::getDelFlag,"0")
                .eq(null != merchantId && merchantId > 0,SysDept::getMerchantId,merchantId));
        JsonResults results = JsonResults.success();
        results.put("depts", deptService.buildDeptTreeSelect(deptList));
        return results;
    }

}
