package com.dfc.ind.mapper.sys;

import com.dfc.ind.entity.sys.SysRole;
import com.dfc.ind.entity.sys.SysRoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色表 数据层
 *
 * @author admin
 */
public interface SysRoleMapper extends BaseMapper<SysRole>
{
    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public List<SysRole> selectRoleList(SysRole role);


    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<SysRole> selectRolePermissionByUserId(Long userId);

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
     * 根据用户ID查询角色
     *
     * @param userName 用户名
     * @return 角色列表
     */
    public List<SysRole> selectRolesByUserName(String userName);

    /**
     * 校验角色名称是否唯一
     *
     * @param roleName 角色名称
     * @return 角色信息
     */
    public SysRole checkRoleNameUnique(String roleName,Long merchantId);

    /**
     * 校验角色权限是否唯一
     *
     * @param roleKey 角色权限
     * @return 角色信息
     */
    public SysRole checkRoleKeyUnique(String roleKey,Long merchantId);

    /**
     * 修改角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public int updateRole(SysRole role);

    /**
     * 新增角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public int insertRole(SysRole role);

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
     * 根据角色类型等级获取用户id集合
     *
     * @param roleTypes
     * @return
     */
    List<String> getUserId(@Param("roleTypes") List<String> roleTypes, @Param("roleType") String roleType);

    /**
     * 根据角色类型等级获取用户id,商户id集合
     *
     * @param roleTypes
     * @return
     */
    List<Map<String, Object>> getUserMerchant(@Param("roleTypes") List<String> roleTypes, @Param("roleType") String roleType);

    /**
     * 根号角色ID查询角色关联菜单
     *
     * @param roleId
     * @return
     */
    List<SysRoleMenu> getByRole(Long roleId);

    /**
     * 新增角色菜单
     *
     * @param roleMenuList
     * @return
     */
    int batchRoleMenu(List<SysRoleMenu> roleMenuList);

    Set<String> getRoleNamesByUerId(Long userId);
}
