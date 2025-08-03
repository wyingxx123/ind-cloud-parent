package com.dfc.ind.service.sys;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.sys.SysUser;
import com.dfc.ind.entity.vo.MallUserEntityVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 用户 业务层
 *
 * @author admin
 */
public interface ISysUserService extends IService<SysUser> {
    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUserList(SysUser user);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    public SysUser selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    public SysUser selectUserById(Long userId);

    /**
     * 根据用户ID查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    public String selectUserRoleGroup(String userName);

    /**
     * 根据用户ID查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    public String selectUserPostGroup(String userName);

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    public String checkUserNameUnique(String userName);

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    public String checkPhoneUnique(SysUser user);

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    public String checkEmailUnique(SysUser user);

    /**
     * 根据商户号查询用户
     *
     * @param merchantId
     * @return
     */
    List<SysUser> getUsersByMerchantId(Long merchantId);

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    public void checkUserAllowed(SysUser user);

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(SysUser user);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int updateUser(SysUser user);

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserStatus(SysUser user);

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserProfile(SysUser user);

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar   头像地址
     * @return 结果
     */
    public boolean updateUserAvatar(String userName, String avatar);

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    public int resetPwd(SysUser user);

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    public int resetUserPwd(String userName, String password);

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteUserById(Long userId);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    public int deleteUserByIds(Long[] userIds);

    /**
     * 导入用户数据
     *
     * @param userList        用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName);

    /**
     * <二维码注册>
     *
     * @param mallUserEntityVO 注册信息封装对象
     * @return
     * @author dingw
     * @Date 2020/5/26 9:47
     */
    JsonResults twoCodeRegister(MallUserEntityVO mallUserEntityVO);

    /**
     * 分页查询
     *
     * @param startPage 分页对象
     * @param entity    条件对象
     * @return 结果
     * @author nwy
     */
    IPage pageList(Page startPage, SysUser entity);


    /**
     * 修改用户类型
     */
    int updateUserType(List<Long> idList, Long merchantId);

    /**
     * 根据条件查询所有用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectAllUser(SysUser user);

    /**
     * 根据角色、商户号查询用户
     *
     * @param merchantId
     * @param roleTypes
     * @return
     */
    public JsonResults getUsersByRolesMerchantId(Long merchantId, String roleTypes);

    /**
     * 获取用户角色详细信息
     *
     * @param roleType
     * @param merchantId
     * @return
     */
    List<Map<String, Object>> getUserRoleInfo(@RequestParam(required = false) String roleType, @RequestParam Long merchantId);

    Page<SysUser> getUserByRoleType(Page startPage,String roleType,String userName,List<Long> deptId,String status,String phonenumber,String beginDate,String endDate, Long merchantId);

    List<SysUser> getUserListByRoleIds(String[] roleIdArr, Long merchantId);

    SysUser selectUserByPoneNumber(String username, String merchantId);

    String queryCurrentUserPermission(SysUser user);

    SysUser selectUserTypeById(Long userId);

    Page<SysUser> getUserByRoleTypeByUserId(Page startPage, String roleType, String userName, List<Long> deptIds, String status, String phonenumber, String beginDate, String endDate, Long merchantId,Long userId) ;

    SysUser selectUserByUserNameAndMerchantId(String username, String merchantId);

    JsonResults sendSms(String phoneNumber, String merchantId);
}

