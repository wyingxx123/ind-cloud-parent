package com.dfc.ind.auth.controller;

import com.alibaba.fastjson.JSON;
import com.dfc.ind.auth.bean.GetPhoneNumberInBean;
import com.dfc.ind.auth.handler.WeChatDecryptDataUtil;
import com.dfc.ind.common.core.constant.Constants;
import com.dfc.ind.common.core.constant.SecurityConstants;
import com.dfc.ind.common.core.domain.R;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.sys.SysUser;
import com.dfc.ind.feign.RemoteLogService;
import com.dfc.ind.feign.RemoteUserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * token 控制
 * 
 * @author admin
 */
@RestController
@RequestMapping("/token")
public class TokenController
{
	
	@Value("${wechat.appid}")
    private String wechatAppId;

    @Value("${wechat.secret}")
    private String wechatSecret;

    @Value("${session.key}")
    private String session_Key;


    @Value("${vxip}")
    private  String vxip;

    @Value("${vxport}")
    private String  vxport;
    
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private RemoteLogService remoteLogService;

    @Autowired
    private RemoteUserService remoteUserService;
    
    private static Map<String, Object> accessTokenMap = new HashMap<String, Object>();
    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @DeleteMapping("/logout")
    public R<?> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader)
    {
        if (StringUtils.isEmpty(authHeader))
        {
            return R.ok();
        }

        String tokenValue = authHeader.replace(OAuth2AccessToken.BEARER_TYPE, StringUtils.EMPTY).trim();
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
        if (accessToken == null || StringUtils.isEmpty(accessToken.getValue()))
        {
            return R.ok();
        }

        // 清空 access token
        tokenStore.removeAccessToken(accessToken);

        // 清空 refresh token
        OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
        tokenStore.removeRefreshToken(refreshToken);
        Map<String, ?> map = accessToken.getAdditionalInformation();
        if (map.containsKey(SecurityConstants.DETAILS_USERNAME))
        {
            String username = (String) map.get(SecurityConstants.DETAILS_USERNAME);
            // 记录用户退出日志
            remoteLogService.saveLogininfor(username, Constants.LOGOUT, "退出成功");
        }
        return R.ok();
    }
    
    @GetMapping("/getWechatToken")
    @ApiOperation(value = "获取微信小程序的token")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "paramsData", value = "参数", required = true, dataType = "String")
    })
    public JsonResults getWechatToken(String paramsData) {
    	String accessToken = (String) accessTokenMap.get("accessToken");
    	Map<String, Object> returnMap = new HashMap<String, Object>();
        try {
            if (accessToken == null || "".equals(accessToken)) {
                accessToken = microPlatformAuth();
            }
        } catch (Exception e) {
        	return JsonResults.error("获取微信小程序认证失败!");
        }
        returnMap.put("accessToken", accessToken);
        return JsonResults.success(returnMap);
    }

    public String microPlatformAuth() throws Exception {
        /*String authUrl = "http://"+vxip+":"+vxport+"/cgi-bin/token?grant_type=client_credential&appid=" + wechatAppId
                + "&secret=" + wechatSecret;*/
        String authUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + wechatAppId
                + "&secret=" + wechatSecret;
        String accessToken = "";
        try {
        	RestTemplate restTemplate = new RestTemplate();
            Map returnMap = restTemplate.getForObject(authUrl, Map.class);
            accessToken = (String) returnMap.get("access_token");
            Integer expiresIn = (Integer) returnMap.get("expires_in");
            Integer errcode = (Integer) returnMap.get("errcode");
            if (errcode != null && !"".equals(errcode)) {
                String errmsg = (String) returnMap.get("errmsg");
                logger.error(errmsg);
                throw new Exception();
            } else {
                accessTokenMap.put("accessToken", accessToken);
                accessTokenMap.put("expiresIn", expiresIn);
            }
        } catch (Exception e) {
            logger.error("获取微信小程序认证失败!", e);
            throw new Exception();
        }
        return accessToken;
    }

    /**
     * 获取微信小程序sessionKey
     *
     * @param inBean
     * @return
     */
    @GetMapping("/jscode2session")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "jsCode", value = "微信token", required = true, dataType = "String")
    })
    public JsonResults jscode2session(String jsCode) {
//        String url = "http://"+vxip+":"+vxport+"/sns/jscode2session?appid=" + wechatAppId + "&secret=" + wechatSecret + "&js_code=" + jsCode + "&grant_type=authorization_code";
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + wechatAppId + "&secret=" + wechatSecret + "&js_code=" + jsCode + "&grant_type=authorization_code";
        Map<String, Object> result = new HashMap<String, Object>();
        try {
        	RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            result = JSON.parseObject(response, Map.class);
        } catch (Exception e) {
            logger.error("获取微信小程序sessionKey失败", e);
            return JsonResults.error("获取微信小程序sessionKey失败!");
        }
        return JsonResults.success(result);
    }
    
    /**
     * 解密微信获取的手机号
     *
     * @param inBean
     * @return
     */
    @PostMapping("/getPhoneNumber")
    public JsonResults getPhoneNumber(@RequestBody GetPhoneNumberInBean inBean) {
        String response = WeChatDecryptDataUtil.decryptData(inBean.getEncryptedData(), inBean.getSessionKey(), inBean.getIv());
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = JSON.parseObject(response, Map.class);
        } catch (Exception e) {
            logger.error("解密手机号失败", e);
            return JsonResults.error("解密手机号失败!");
        }
        return JsonResults.success(result);
    }
    
    /**
     * 通过微信登录后免密登录系统获取session
     *
     * @param inBean
     * @return
     */
    @PostMapping("/wechatLoginToken")
    public JsonResults wechatLoginToken(@RequestBody GetPhoneNumberInBean inBean) {
    	//获取微信sessionkey
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + wechatAppId + "&secret=" + wechatSecret + "&js_code=" + inBean.getJsCode() + "&grant_type=authorization_code";
//        String url = "http://"+vxip+":"+vxport+"/sns/jscode2session?appid=" + wechatAppId + "&secret=" + wechatSecret + "&js_code=" + inBean.getJsCode() + "&grant_type=authorization_code";
        Map<String, Object> result = new HashMap<String, Object>();
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(url, String.class);
            result = JSON.parseObject(response, Map.class);
            System.out.println("获取情况："+result);
        } catch (Exception e) {
            logger.error("获取微信小程序sessionKey失败", e);
            return JsonResults.error("获取微信小程序端sessionKey失败!"+"获取情况："+result+wechatAppId);
        }
        if (result.containsKey("errcode")) {
            return JsonResults.error("获取微信小程序端sessionKey失败!"+"获取情况："+result+"Secrt="+wechatSecret+"appid="+wechatAppId+"ip="+vxip+"port="+vxport+"jsCode="+inBean.getJsCode());
        }
        //解密获取微信绑定的手机号码
        String sessionKey = (String)result.get(session_Key);
        String openId = (String)result.get("openid");
        try {
        	String response = WeChatDecryptDataUtil.decryptData(inBean.getEncryptedData(), sessionKey, inBean.getIv());
            result = JSON.parseObject(response, Map.class);
        } catch (Exception e) {
            logger.error("解密手机号失败", e);
            return JsonResults.error("解密手机号失败!");
        }
        logger.info("解密后结果："+result.toString());
        System.out.println("解密后结果："+result.toString());
        String phoneNumber = (String)result.get("phoneNumber");
        //通过手机号码调用内部免密登录
        String loginUrl = "http://127.0.0.1:8603/ind-auth/oauth/token?username=" + phoneNumber 
        		+ "&password=&grant_type=password&scope=server&client_id=web&client_secret=123456&loginFlag=false";
        Map<String, Object> loginResult = restTemplate.getForObject(loginUrl, Map.class);
        loginResult.put("sessionKey", sessionKey);

        try {
            //通过授权补充openId信息
            List<SysUser> list = remoteUserService.getUserForPhone(phoneNumber);
            if (list.size()>0) {
                for(SysUser user:list){
                        user.setOpenId(openId);
                        remoteUserService.editUserForPhone(user);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("补充openid失败", e);
        }

        return JsonResults.success(loginResult);
    }



    
}
