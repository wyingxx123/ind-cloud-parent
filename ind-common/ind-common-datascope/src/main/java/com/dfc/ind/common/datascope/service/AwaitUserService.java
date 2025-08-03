package com.dfc.ind.common.datascope.service;

import com.dfc.ind.feign.RemoteUserService;
import com.dfc.ind.sys.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dfc.ind.common.core.domain.R;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.security.utils.SecurityUtils;


/**
 * 同步调用用户服务
 * 
 * @author admin
 */
@Service
public class AwaitUserService
{
    private static final Logger log = LoggerFactory.getLogger(AwaitUserService.class);

    @Autowired
    private RemoteUserService remoteUserService;

    /**
     * 查询当前用户信息
     * 
     * @return 用户基本信息
     */
    public UserInfo info()
    {
        String username = SecurityUtils.getUsername();
        R<UserInfo> userResult = remoteUserService.getUserInfo(username);
        if (StringUtils.isNull(userResult) || StringUtils.isNull(userResult.getData()))
        {
            log.info("数据权限范围查询用户：{} 不存在.", username);
            return null;
        }
        return userResult.getData();
    }
}
