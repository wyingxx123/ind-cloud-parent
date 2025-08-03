package com.dfc.ind.service.sys;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.sys.SysRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dfc.ind.entity.sys.SysUser;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色业务层
 *
 * @author admin
 */
public interface ISysRoleService extends IService<SysRole> {
    /**
     * 根据条件分页查询角色数据
     *
     * @return 角色数据集合信息
     */
    public IPage<SysRole> pageList(Page startPage, SysRole role);

    /**
     * 根据用户ID查询角色类型
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public Set<String> selectRolePermissionByUserId(Long userId);

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public Set<String> selectRoleByUserId(Long userId);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    public List<SysRole> selectRoleAll();

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    public List<Integer> selectRoleListByUserId(Long userId);

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    public SysRole selectRoleById(Long roleId);

    /**
     * 通过角色ID查询角色号
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    public List<Integer> selectRoleIdsByUserId(Long roleId);

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    public String checkRoleNameUnique(SysRole role);

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    public String checkRoleKeyUnique(SysRole role);

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    public void checkRoleAllowed(SysRole role);

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    public int countUserRoleByRoleId(Long roleId);

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public int insertRole(SysRole role);

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public int updateRole(SysRole role);

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    public int updateRoleStatus(SysRole role);

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public int authDataScope(SysRole role);

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    public int deleteRoleById(Long roleId);

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    public int deleteRoleByIds(Long[] roleIds);

    /**
     * 查询全部角色
     *
     * @param entity
     * @return
     */
    JsonResults listAll(SysRole entity);

    /**
     * 获取二维码
     *
     * @param entity
     * @return
     */
    JsonResults twoCode(SysRole entity);

    /**
     * 根据角色类型等级获取用户id集合
     *
     * @param roleType
     * @return
     */
    List<String> getUserId(String roleType);


    /**
     * 根据角色类型等级获取用户id,商户id集合
     *
     * @param roleType
     * @return
     */
    List<Map<String, Object>> getUserMerchant(String roleType);

    /**
     * 查询用户所属角色组
     *
     * @param userId 用户id
     * @return 结果
     */
    String selectRolesByUserId(Long userId);

    /**
     * 根据角色类型查询角色ID
     *
     * @param roleTypes
     * @return
     */
    JsonResults getRoleIdsByList(String roleTypes, String roleIds);

    Set<String> getRoleNamesByUerId(Long userId);


}
