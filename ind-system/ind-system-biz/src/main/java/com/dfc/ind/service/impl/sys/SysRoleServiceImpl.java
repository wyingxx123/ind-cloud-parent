package com.dfc.ind.service.impl.sys;



import com.dfc.ind.common.core.constant.CommonConstants;
import com.dfc.ind.common.core.constant.UserConstants;
import com.dfc.ind.common.core.exception.CustomException;
import com.dfc.ind.common.core.text.Convert;
import com.dfc.ind.common.core.utils.SpringUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.sys.SysDept;
import com.dfc.ind.entity.sys.SysRole;
import com.dfc.ind.entity.sys.SysRoleDept;
import com.dfc.ind.entity.sys.SysRoleMenu;
import com.dfc.ind.mapper.sys.SysRoleDeptMapper;
import com.dfc.ind.mapper.sys.SysRoleMapper;
import com.dfc.ind.mapper.sys.SysRoleMenuMapper;
import com.dfc.ind.mapper.sys.SysUserRoleMapper;
import com.dfc.ind.service.sys.ISysDeptService;
import com.dfc.ind.service.sys.ISysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色 业务层处理
 *
 * @author admin
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {
    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleDeptMapper roleDeptMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    public IPage<SysRole> pageList(Page startPage, SysRole role) {
        return page(startPage, queryWrapper(role));
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        List<SysRole> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (StringUtils.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRoleType().trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public Set<String> selectRoleByUserId(Long userId) {
        List<SysRole> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (StringUtils.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRoleId().toString()));
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRoleAll() {
        return SpringUtils.getAopProxy(this).list();
    }

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    @Override
    public List<Integer> selectRoleListByUserId(Long userId) {
        return roleMapper.selectRoleListByUserId(userId);
    }

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public SysRole selectRoleById(Long roleId) {
        return roleMapper.selectRoleById(roleId);
    }

    /**
     * 通过角色ID查询角色
     *
     * @param userId 角色ID
     * @return 角色对象信息
     */
    @Override
    public List<Integer> selectRoleIdsByUserId(Long userId) {
        return roleMapper.selectRoleIdsByUserId(userId);
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public String checkRoleNameUnique(SysRole role) {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        Long merchantId = SecurityUtils.getLoginUser().getMerchantId();
        SysRole info = roleMapper.checkRoleNameUnique(role.getRoleName(), merchantId);
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public String checkRoleKeyUnique(SysRole role) {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        Long merchantId = SecurityUtils.getLoginUser().getMerchantId();
        SysRole info = roleMapper.checkRoleKeyUnique(role.getRoleKey(), merchantId);
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(SysRole role) {
        if (StringUtils.isNotNull(role.getRoleId()) && role.isAdmin()) {
            throw new CustomException("不允许操作超级管理员角色");
        }
    }

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public int countUserRoleByRoleId(Long roleId) {
        return userRoleMapper.countUserRoleByRoleId(roleId);
    }

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertRole(SysRole role) {
        // 新增角色信息
        roleMapper.insertRole(role);
        return insertRoleMenu(role);
    }

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateRole(SysRole role) {
        // 修改角色信息
        roleMapper.updateRole(role);
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleId(role.getRoleId());
        return insertRoleMenu(role);
    }

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public int updateRoleStatus(SysRole role) {
        return roleMapper.updateRole(role);
    }

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int authDataScope(SysRole role) {
        // 修改角色信息
        roleMapper.updateRole(role);
        // 删除角色与部门关联
        roleDeptMapper.deleteRoleDeptByRoleId(role.getRoleId());
        // 新增角色和部门信息（数据权限）
        return insertRoleDept(role);
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    public int insertRoleMenu(SysRole role) {
        int rows = 1;
        // 新增用户与角色管理
        List<SysRoleMenu> list = new ArrayList<SysRoleMenu>();
        for (Long menuId : role.getMenuIds()) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0) {
            rows = roleMenuMapper.batchRoleMenu(list);
        }
        return rows;
    }

    /**
     * 新增角色部门信息(数据权限)
     *
     * @param role 角色对象
     */
    public int insertRoleDept(SysRole role) {
        int rows = 1;
        // 新增角色与部门（数据权限）管理
        List<SysRoleDept> list = new ArrayList<SysRoleDept>();
        for (Long deptId : role.getDeptIds()) {
            SysRoleDept rd = new SysRoleDept();
            rd.setRoleId(role.getRoleId());
            rd.setDeptId(deptId);
            list.add(rd);
        }
        if (list.size() > 0) {
            rows = roleDeptMapper.batchRoleDept(list);
        }
        return rows;
    }

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public int deleteRoleById(Long roleId) {
        return roleMapper.deleteRoleById(roleId);
    }

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @Override
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            checkRoleAllowed(new SysRole(roleId));
            SysRole role = selectRoleById(roleId);
            if (countUserRoleByRoleId(roleId) > 0) {
                throw new CustomException(String.format("%1$s已分配,不能删除", role.getRoleName()));
            }
        }
        return roleMapper.deleteRoleByIds(roleIds);
    }

    @Override
    public JsonResults listAll(SysRole entity) {
        return JsonResults.success(this.list(queryWrapper(entity)));
    }


    @Override
    public JsonResults twoCode(SysRole entity) {
        StringBuffer url = new StringBuffer();
        url.append(entity.getRoleId() + "&");
        if (entity.getMerchantId() != null && entity.getMerchantId() > 0) {
            Long deptId = sysDeptService.getOne(new QueryWrapper<SysDept>().lambda()
                    .eq(SysDept::getMerchantId, entity.getMerchantId())
                    .eq(SysDept::getParentId, "0")
                    .eq(SysDept::getDelFlag, "0")).getDeptId();
            deptId = deptId != null ? deptId : 0;
            url.append(deptId + "&");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            url.append(simpleDateFormat.format(entity.getCreateTime()) + "&");
            url.append(entity.getMerchantId() + "&");
            byte[] encode = Base64Utils.encode(url.toString().getBytes());
            entity.setTwoCode("http://192.168.4.47:8080/#/registered/&" + new String(encode));
            if (this.updateById(entity)) {
                return JsonResults.success("操作成功", entity.getTwoCode());
            }
            return JsonResults.error();
        } else {
            return JsonResults.error("系统角色不能生成二维码！");
        }

    }

    @Override
    public List<String> getUserId(String roleType) {
        List<String> roleTypes = new ArrayList<>();
        if (!CommonConstants.RoleType.TYPE_0.equals(roleType)) {
            String[] array = Convert.toStrArray(roleType);
            Collections.addAll(roleTypes, array);
            roleType = "1";
        }
        return baseMapper.getUserId(roleTypes, roleType);
    }

    @Override
    public List<Map<String, Object>> getUserMerchant(String roleType) {
        List<String> roleTypes = new ArrayList<>();
        if (!CommonConstants.RoleType.TYPE_0.equals(roleType)) {
            String[] array = Convert.toStrArray(roleType);
            Collections.addAll(roleTypes, array);
            roleType = "1";
        }
        return baseMapper.getUserMerchant(roleTypes, roleType);
    }

    @Override
    public String selectRolesByUserId(Long userId) {
        List<SysRole> sysRoleEntities = baseMapper.selectRolePermissionByUserId(userId);
        StringBuffer sb = new StringBuffer();
        for (SysRole role : sysRoleEntities) {
            sb.append(role.getRoleName()).append(",");
        }
        if (StringUtils.isNotEmpty(sb.toString())) {
            return sb.substring(0, sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public JsonResults getRoleIdsByList(String roleTypes, String roleIds) {
        List<SysRole> sysRoles = new ArrayList<>();
        List<String> result = new ArrayList<>();
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(SysRole::getMerchantId, SecurityUtils.getLoginUser().getMerchantId());
        //传入角色类型返回角色编号；传入角色编号则返回角色类型
        if (StringUtils.isNotEmpty(roleTypes)) {
            queryWrapper.lambda().in(SysRole::getRoleType, Arrays.asList(roleTypes.split(",")));
            sysRoles = this.list(queryWrapper);
            sysRoles.forEach(role -> result.add(role.getRoleId().toString()));
        } else if (StringUtils.isNotEmpty(roleIds)) {
            queryWrapper.lambda().in(SysRole::getRoleId, Arrays.asList(roleIds.split(",")));
            sysRoles = this.list(queryWrapper);
            sysRoles.forEach(role -> result.add(role.getRoleType().toString()));
        }
        return JsonResults.success(result);
    }

    @Override
    public Set<String> getRoleNamesByUerId(Long userId) {
        return baseMapper.getRoleNamesByUerId(userId);
    }

    private QueryWrapper<SysRole> queryWrapper(SysRole entity) {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysRole::getDelFlag, 0)
                .like(StringUtils.isNotEmpty(entity.getRoleName()), SysRole::getRoleName, entity.getRoleName())
                .like(StringUtils.isNotEmpty(entity.getRoleKey()), SysRole::getRoleKey, entity.getRoleKey())
                .eq(StringUtils.isNotEmpty(entity.getStatus()), SysRole::getStatus, entity.getStatus())
                .eq(StringUtils.isNotEmpty(entity.getRoleType()), SysRole::getRoleType, entity.getRoleType())
                .ge(StringUtils.isNotEmpty(entity.getBeginDate()), SysRole::getCreateTime, entity.getBeginDate())
                .le(StringUtils.isNotEmpty(entity.getEndDate()), SysRole::getCreateTime, entity.getEndDate())
                .eq(null != entity.getMerchantId() && entity.getMerchantId() > 0, SysRole::getMerchantId, entity.getMerchantId());
        return wrapper;
    }
}
