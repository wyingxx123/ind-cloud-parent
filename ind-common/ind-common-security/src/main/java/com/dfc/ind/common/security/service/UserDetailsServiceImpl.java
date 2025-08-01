package com.dfc.ind.common.security.service;

import com.dfc.ind.common.core.domain.R;
import com.dfc.ind.common.core.enums.UserStatus;
import com.dfc.ind.common.core.exception.CustomException;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.security.domain.LoginUser;
import com.dfc.ind.entity.sys.SysUser;
import com.dfc.ind.feign.RemoteUserService;
import com.dfc.ind.sys.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * 用户信息处理
 *
 * @author admin
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private RemoteUserService remoteUserService;

    @Override
    public UserDetails loadUserByUsername(String username)
    {
        R<UserInfo> userResult = null;
        String merchantId=null;
        if (username.contains(":")){
            String[] split = username.split(":");
            username=split[0];
            merchantId=split[1];
            if (StringUtils.isEmpty(merchantId)){
                log.error("登录用户：{}商户号不能为空.", username);
                throw new UsernameNotFoundException("登录用户：" + username + "商户号不能为空");
            }
            //先验证是否手机号登录，设置跳过密码验证，否则执行原用户密码登录流程
            userResult = remoteUserService.getUserByPhoneAndMerchantId(username,merchantId);
            if (StringUtils.isNull(userResult.getData())) {
                userResult = remoteUserService.getUserInfoAndMerchantId(username,merchantId);
            }
        }else {
            log.error("登录用户：{}商户号不能为空.", username);
            throw new UsernameNotFoundException("登录用户：" + username + "商户号不能为空");
        }

        checkUser(userResult, username);
        return getUserDetails(userResult);
    }

    public void checkUser(R<UserInfo> userResult, String username)
    {
        if (StringUtils.isNull(userResult) || StringUtils.isNull(userResult.getData()))
        {
            log.info("登录账号：{} 不存在.", username);
            throw new CustomException("用户：" + username + " 不存在,请检查账号和商户号");
        }
        else if (UserStatus.DELETED.getCode().equals(userResult.getData().getSysUser().getDelFlag()))
        {
            log.info("登录账号：{} 已被删除.", username);
            throw new CustomException("对不起，您的账号：" + username + " 已被删除");
        }
        else if (UserStatus.DISABLE.getCode().equals(userResult.getData().getSysUser().getStatus()))
        {
            log.info("登录账号：{} 已被停用.", username);
            throw new CustomException("对不起，您的账号：" + username + " 已停用");
        }
    }

    private UserDetails getUserDetails(R<UserInfo> result)
    {
        UserInfo info = result.getData();
        Set<String> dbAuthsSet = new HashSet<String>();
        if (StringUtils.isNotEmpty(info.getRoles()))
        {
            // 获取角色
            dbAuthsSet.addAll(info.getRoles());
            // 获取权限
            dbAuthsSet.addAll(info.getPermissions());
        }

        Collection<? extends GrantedAuthority> authorities = AuthorityUtils
                .createAuthorityList(dbAuthsSet.toArray(new String[0]));
        SysUser user = info.getSysUser();

        return new LoginUser(user.getUserId(), user.getMerchantId(), user.getUserName(), user.getPassword(), true, true, true, true,
                authorities);
    }
}
