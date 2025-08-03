package com.dfc.ind.auth.config;

import com.dfc.ind.auth.exception.CustomOauthException;
import com.dfc.ind.auth.utils.CryptoUtils;
import com.dfc.ind.common.core.exception.CustomException;
import com.dfc.ind.common.core.utils.secret.AESUtil;
import com.dfc.ind.common.redis.service.RedisService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.util.Assert;

import java.util.Map;

@Configuration
@EnableAuthorizationServer
public class LoginAuthenticationProvider extends DaoAuthenticationProvider {

	@Autowired
	private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisService redisService;

	private static final String UNIQUE_KEY="0102030405060708";
	public LoginAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, RedisService redisService) {
        super();
        // 这个地方一定要对userDetailsService赋值，不然userDetailsService是null (这个坑有点深)
        setUserDetailsService(userDetailsService);
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.passwordEncoder = passwordEncoder;
        this.redisService=redisService;
    }
    @SneakyThrows
    @SuppressWarnings("deprecation")
    protected void additionalAuthenticationChecks(UserDetails userDetails,
        UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");
            throw new BadCredentialsException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        Map<String, Object> loginMap = (Map<String, Object>)authentication.getDetails();
        if (loginMap.get("loginType")!=null&&"sms".equals(loginMap.get("loginType"))){
            //验证码通道
          try {
              if (loginMap.get("smsCode")!=null){
                  String smsCode = loginMap.get("smsCode").toString();
                  //短息验证
                  String code = redisService.getCacheObject(loginMap.get("username").toString()+":smsCode");
                  if (smsCode.equals(code)){
                      //校验通过
                      return;
                  }else {
                      throw new CustomException("验证码错误或已过期,请重新获取验证码");
                  }
              }
          }catch (Exception e){
              e.printStackTrace();
              throw new CustomException("验证码错误");
          }

        }
        String presentedPassword = authentication.getCredentials().toString();
        //=================================解决渗透安全 登录加密
        //解密：
        try {
            presentedPassword= AESUtil.decrypt(presentedPassword, UNIQUE_KEY);
            if (presentedPassword==null){
                throw new CustomException("密码校验失败");
            }
        }catch (Exception e){
            logger.error("密码校验失败",e);

            throw new CustomException("密码校验失败");
        }
        boolean userFlag = loginMap.get("loginFlag")==null?true:Boolean.valueOf(loginMap.get("loginFlag").toString());
        if (userFlag) {
        	if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
    			logger.debug("Authentication failed: password does not match stored value");

    			throw new BadCredentialsException(messages.getMessage(
    					"AbstractUserDetailsAuthenticationProvider.badCredentials",
    					"Bad credentials"));
    		}
        }
    }
}
