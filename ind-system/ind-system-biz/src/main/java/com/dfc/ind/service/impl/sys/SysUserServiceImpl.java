package com.dfc.ind.service.impl.sys;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfc.ind.common.core.constant.UserConstants;
import com.dfc.ind.common.core.exception.CustomException;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.IdUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.datascope.annotation.DataScope;
import com.dfc.ind.common.redis.service.RedisService;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.sys.SysDept;
import com.dfc.ind.entity.sys.SysPost;
import com.dfc.ind.entity.sys.SysRole;
import com.dfc.ind.entity.sys.SysUser;
import com.dfc.ind.entity.sys.SysUserPost;
import com.dfc.ind.entity.sys.SysUserRole;
import com.dfc.ind.entity.vo.MallUserEntityVO;
import com.dfc.ind.mapper.sys.SysPostMapper;
import com.dfc.ind.mapper.sys.SysRoleMapper;
import com.dfc.ind.mapper.sys.SysUserMapper;
import com.dfc.ind.mapper.sys.SysUserPostMapper;
import com.dfc.ind.mapper.sys.SysUserRoleMapper;
import com.dfc.ind.service.sys.ISysConfigService;
import com.dfc.ind.service.sys.ISysDeptService;
import com.dfc.ind.service.sys.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20190711.models.SendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户 业务层处理
 *
 * @author admin
 */
@Service
@RefreshScope
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private RedisService redisService;
    //腾讯云秘钥id

    @Value("${sms.secretId}")
    private String smsSecretId;
    //腾讯云秘钥key
    @Value("${sms.secretKey}")
    private String smsSecretKey;
    //短信模板id
    @Value("${sms.templateId}")
    private String smsTemplateId;
    //短信应用id
    @Value("${sms.appId}")
    private String smsAppId;
    //短息签名名称
    @Value("${sms.sign}")
    private String smsSign;

    //验证码过期时间
    @Value("${sms.timeOut}")
    private String smsTimeOut;
    @Override
    @Transactional
    public int insertUser(SysUser entity) {
            entity.setUserType("01");
                //处理部门
        if (StringUtils.isNotEmpty(entity.getDeptName())){
            SysDept dept= deptService.getByDeptName(entity.getDeptName(),entity.getMerchantId());
            entity.setDeptId(dept.getDeptId());
        }
        entity.setDelFlag("0");
        int rows = baseMapper.insertUser(entity);
        // 新增用户岗位关联
        insertUserPost(entity);
        // 新增用户与角色管理
        insertUserRole(entity);
        return rows;
    }

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUserList(SysUser user) {
        return userMapper.selectUserList(user);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {

        return userMapper.selectUserByUserName(userName,SecurityUtils.getLoginUser()==null?null:SecurityUtils.getLoginUser().getMerchantId());
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName) {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        StringBuffer idsStr = new StringBuffer();
        for (SysRole role : list) {
            idsStr.append(role.getRoleName()).append(",");
        }
        if (StringUtils.isNotEmpty(idsStr.toString())) {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName) {
        List<SysPost> list = postMapper.selectPostsByUserName(userName);
        StringBuffer idsStr = new StringBuffer();
        for (SysPost post : list) {
            idsStr.append(post.getPostName()).append(",");
        }
        if (StringUtils.isNotEmpty(idsStr.toString())) {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    @Override
    public String checkUserNameUnique(String userName) {
        int count = userMapper.checkUserNameUnique(userName);
        if (count > 0) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        List<SysUser> info = userMapper.checkPhoneUnique(user.getPhonenumber());
       /* if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }*/
       if(info.size()>0){
           return UserConstants.NOT_UNIQUE;
       }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user) {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new CustomException("不允许操作超级管理员用户");
        }
    }


    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateUser(SysUser user) {
        Long userId = user.getUserId();
        int MSG=0;
        this.checkUserAllowed(user);
        SysUser sysUser = userMapper.selectUserById(userId);
        if(sysUser.getPhonenumber().equals(user.getPhonenumber())){
            // 删除用户与角色关联
            userRoleMapper.deleteUserRoleByUserId(userId);
            // 新增用户与角色管理
            insertUserRole(user);
            // 删除用户与岗位关联
            userPostMapper.deleteUserPostByUserId(userId);
            // 新增用户与岗位管理
            insertUserPost(user);
            userMapper.updateUser(user);
            MSG=2;
        }else
        if(StringUtils.isNotEmpty(user.getPhonenumber()) && UserConstants.NOT_UNIQUE.equals(this.checkPhoneUnique(user))){
            // 删除用户与角色关联
            userRoleMapper.deleteUserRoleByUserId(userId);
            // 新增用户与角色管理
            insertUserRole(user);
            // 删除用户与岗位关联
            userPostMapper.deleteUserPostByUserId(userId);
            // 新增用户与岗位管理
            insertUserPost(user);
            userMapper.updateUserorPhone(user);
            MSG=1;

        }else{
            // 删除用户与角色关联
            userRoleMapper.deleteUserRoleByUserId(userId);
            // 新增用户与角色管理
            insertUserRole(user);
            // 删除用户与岗位关联
            userPostMapper.deleteUserPostByUserId(userId);
            // 新增用户与岗位管理
            insertUserPost(user);
            userMapper.updateUser(user);
            MSG=2;
        }
        return MSG;
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user) {
        return userMapper.updateUser(user);
    }

    @Override
    public List<SysUser> getUsersByMerchantId(Long merchantId) {
        return this.list(new QueryWrapper<SysUser>().lambda().eq(SysUser::getMerchantId, merchantId)
                .eq(SysUser::getDelFlag, "0"));
    }

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar   头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String userName, String avatar) {
        return userMapper.updateUserAvatar(userName, avatar) > 0;
    }

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(String userName, String password) {
        return userMapper.resetUserPwd(userName, password);
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user) {
        Long[] roles = user.getRoleIds();
        if (StringUtils.isNotEmpty(user.getRoleName())){
            String[] split = user.getRoleName().split(",");
            LambdaQueryWrapper<SysRole> wrapper = new QueryWrapper<SysRole>().lambda().in(SysRole::getRoleName, split)
                    .eq(SysRole::getMerchantId,user.getMerchantId());
            List<SysRole> sysRoles = roleMapper.selectList(wrapper);
            if (!CollectionUtils.isEmpty(sysRoles)){
                List<Long> list = sysRoles.stream().map(SysRole::getRoleId).collect(Collectors.toList());
                Long[] arr=new Long[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    arr[i]=list.get(i);
                }
                roles=arr;
            }
        }
        if (StringUtils.isNotNull(roles)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            for (Long roleId : roles) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getUserId());
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0) {
                userRoleMapper.batchUserRole(list);
            }
        }
    }

    /**
     * 新增用户岗位信息
     *
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user) {
        Long[] posts = user.getPostIds();
        if (StringUtils.isNotNull(posts)) {
            // 新增用户与岗位管理
            List<SysUserPost> list = new ArrayList<SysUserPost>();
            for (Long postId : posts) {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }
            if (list.size() > 0) {
                userPostMapper.batchUserPost(list);
            }
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int deleteUserById(Long userId) {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 删除用户与岗位表
        userPostMapper.deleteUserPostByUserId(userId);
        return userMapper.deleteUserById(userId);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            checkUserAllowed(new SysUser(userId));
        }
        return userMapper.deleteUserByIds(userIds);
    }

    /**
     * 导入用户数据
     *
     * @param userList        用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(userList) || userList.size() == 0) {
            throw new CustomException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        Long merchantId = SecurityUtils.getLoginUser().getMerchantId();
        String password = configService.selectConfigByKey("sys.user.initPassword");
        for (SysUser user : userList) {
            try {
                // 验证是否存在这个用户
                SysUser u = userMapper.selectUserByUserName(user.getUserName(),merchantId);
                if (StringUtils.isNull(u)) {
                    user.setPassword(SecurityUtils.encryptPassword(password));
                    user.setCreateBy(operName);
                    user.setMerchantId(merchantId);
                    this.insertUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 导入成功");
                } else if (isUpdateSupport) {
                    user.setUpdateBy(operName);
                    this.updateUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、账号 " + user.getUserName() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new CustomException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    @Override
    public JsonResults twoCodeRegister(MallUserEntityVO mallUserEntityVO) {
        if (mallUserEntityVO.getTwoCode() != null) {
            byte[] decode = Base64Utils.decode(mallUserEntityVO.getTwoCode().getBytes());
            String twoCode = new String(decode);
            String[] str = twoCode.split("&");
            Long roleId = Long.parseLong(str[0]);
            Long deptId = Long.parseLong(str[1]);
            Long merchantId = Long.parseLong(str[3]);
            SysUser mallUserEntity = new SysUser();
            mallUserEntity.setMerchantId(merchantId);
            mallUserEntity.setDeptId(deptId);
            mallUserEntity.setCreateTime(DateUtils.getNowDate());
            mallUserEntity.setUserName(mallUserEntityVO.getUserName());
            mallUserEntity.setPassword(SecurityUtils.encryptPassword(mallUserEntityVO.getPassword()));
            mallUserEntity.setPhonenumber(mallUserEntityVO.getPhoneNumber());
            mallUserEntity.setCreateBy("二维码自主注册");
            if (baseMapper.insert(mallUserEntity) <= 0) {
                return JsonResults.error("注册失败");
            }
            mallUserEntity.setRoleIds(new Long[]{roleId});
            insertUserRole(mallUserEntity);
            return JsonResults.success("注册成功");
        }
        return JsonResults.error("注册失败");
    }

    @Override
    public int updateUserType(List<Long> idList, Long merchantId) {
        return baseMapper.updateUserType(idList, merchantId);
    }

    @Override
    public IPage<SysUser> pageList(Page startPage, SysUser entity) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .likeRight(!StringUtils.isEmpty(entity.getUserName()), SysUser::getUserName, entity.getUserName())
                .likeRight(!StringUtils.isEmpty(entity.getPhonenumber()), SysUser::getPhonenumber, entity.getPhonenumber())
                .eq(!StringUtils.isEmpty(entity.getDelFlag()), SysUser::getDelFlag, entity.getDelFlag())
                .eq(!StringUtils.isEmpty(entity.getStatus()), SysUser::getStatus, entity.getStatus())
                .in(!StringUtils.isEmpty(entity.getDeptIds()), SysUser::getDeptId, entity.getDeptIds())
                .eq(null != entity.getMerchantId() && entity.getMerchantId() > 0, SysUser::getMerchantId, entity.getMerchantId())
                .ge(null != entity.getBeginDate() && !"".equals(entity.getBeginDate()), SysUser::getCreateTime, entity.getBeginDate())
                .le(null != entity.getEndDate() && !"".equals(entity.getEndDate()), SysUser::getCreateTime, entity.getEndDate())
                .orderByDesc(SysUser::getCreateTime);
        return this.page(startPage, queryWrapper);
    }

    @Override
    public List<SysUser> selectAllUser(SysUser entity) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .likeRight(!StringUtils.isEmpty(entity.getUserName()), SysUser::getUserName, entity.getUserName())
                .likeRight(!StringUtils.isEmpty(entity.getPhonenumber()), SysUser::getPhonenumber, entity.getPhonenumber())
                .eq(!StringUtils.isEmpty(entity.getDelFlag()), SysUser::getDelFlag, entity.getDelFlag())
                .eq(!StringUtils.isEmpty(entity.getStatus()), SysUser::getStatus, entity.getStatus())
                .in(!StringUtils.isEmpty(entity.getDeptIds()), SysUser::getDeptId, entity.getDeptIds())
                .eq(null != entity.getMerchantId() && entity.getMerchantId() > 0, SysUser::getMerchantId, entity.getMerchantId())
                .ge(null != entity.getBeginDate() && !"".equals(entity.getBeginDate()), SysUser::getCreateTime, entity.getBeginDate())
                .le(null != entity.getEndDate() && !"".equals(entity.getEndDate()), SysUser::getCreateTime, entity.getEndDate())
                .orderByDesc(SysUser::getCreateTime);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public JsonResults getUsersByRolesMerchantId(Long merchantId, String roleIds) {
        return JsonResults.success(baseMapper.getUsersByRolesMerchantId(Arrays.asList(roleIds.split(",")), merchantId));
    }

    @Override
    public List<Map<String, Object>> getUserRoleInfo(String roleType, Long merchantId) {
        return baseMapper.getUserRoleInfo(roleType, merchantId);
    }

    @Override
    public Page<SysUser> getUserByRoleType(Page startPage,String roleType,String userName,List<Long> deptIds,String status,String phonenumber,String beginDate,String endDate, Long merchantId) {

        return baseMapper.getUserByRoleType(startPage,roleType,userName,deptIds,status,phonenumber,beginDate,endDate, merchantId);
    }

    @Override
    public List<SysUser> getUserListByRoleIds(String[] roleIdArr, Long merchantId) {

        return baseMapper.getUserListByRoleIds(roleIdArr,merchantId);
    }

    @Override
    public SysUser selectUserByPoneNumber(String username, String merchantId) {
        return userMapper.selectUserByPoneNumber(username,merchantId);
    }

    @Override
    public String queryCurrentUserPermission(SysUser user) {

        return userMapper.queryCurrentUserPermission(user);
    }

    @Override
    public SysUser selectUserTypeById(Long userId) {
        return userMapper.selectUserTypeById(userId);
    }

    @Override
    public Page<SysUser> getUserByRoleTypeByUserId(Page startPage, String roleType, String userName, List<Long> deptIds, String status, String phonenumber, String beginDate, String endDate, Long merchantId,Long userId) {
        return baseMapper.getUserByRoleTypeByUserId(startPage,roleType,userName,deptIds,status,phonenumber,beginDate,endDate, merchantId,userId) ;
    }

    @Override
    public SysUser selectUserByUserNameAndMerchantId(String username, String merchantId) {
        return userMapper.selectUserByUserName(username,Long.parseLong(merchantId));

    }

    @Override
    public JsonResults sendSms(String phoneNumber, String merchantId) {
        try{
            QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SysUser::getDelFlag,"0")
                    .eq(SysUser::getPhonenumber,phoneNumber)
                    .eq(SysUser::getMerchantId,merchantId)
                    .eq(SysUser::getStatus,"0");
            if (count(queryWrapper)==0){
                JsonResults.error("该商户下不存在此用户");
            }
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(smsSecretId, smsSecretKey);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
            SendSmsRequest req = new SendSmsRequest();
            // 接收短信的手机号码，可以设置多个
            String[] phoneNumberSet1 = {"+86"+phoneNumber};
            req.setPhoneNumberSet(phoneNumberSet1);

            // 设置正文模板 ID
            req.setTemplateID(smsTemplateId);
            // 设置短信应用 ID
            req.setSmsSdkAppid(smsAppId);
            // 设置签名内容
            req.setSign(smsSign);

            // 调用生成验证码的工具类
            String code = IdUtils.keyUtils();
            // 短信模板中的参数，需与短信模板中的参数个数一致
            String[] templateParams = {code,smsTimeOut};
            req.setTemplateParamSet(templateParams);
            // 发送短信
            SendSmsResponse resp = client.SendSms(req);
            SendStatus[] sendStatusSet = resp.getSendStatusSet();
            for (SendStatus sendStatus : sendStatusSet) {
                if ("Ok".equals(sendStatus.getCode())){
                    String redisKey=phoneNumber+":"+merchantId+":smsCode";
                    redisService.setCacheObject(redisKey,code);
                    redisService.expire(redisKey,Integer.parseInt(smsTimeOut), TimeUnit.MINUTES);
                    log.info("给{}发送短息信息成功,验证码:{}",phoneNumber,code);
                    return  JsonResults.success("发送短息信息成功");
                }else  {
                    return  JsonResults.error("发送短息信息失败");
                }
            }
        } catch (TencentCloudSDKException e) {
            log.error("给{}发送短息信息失败",phoneNumber,e);
            throw new CustomException("发送短息信息失败");
        }
        return  JsonResults.error("发送短息信息失败");
    }
}
