package com.dfc.ind.common.core.utils.sign;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 调用API JWT鉴权工具类
 * @author zhouzhenhui
 */
@Component
@Slf4j
public final class CallApiJwtUtil {

    /**
     * token名
     */
    public static final String TOKEN_NAME = "access-token";
    /**
     * 秘钥
     */
    public static String SECRET = "E7!zR9bFy#f!4HfBwCmK4H38G74*W3e6";
    /**
     * key
     */
    private static final String KEY = "app-code";
    /**
     * 过期时间为7天
     */
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;
    /**
     * jwt秘钥
     */
    @Value("${callApiJwtSecret:E7!zR9bFy#f!4HfBwCmK4H38G74*W3e6}")
    private String jwtSecret;

    @PostConstruct
    public void init() {
        CallApiJwtUtil.SECRET = this.jwtSecret;
    }

    /**
     * 签名
     * @param appCode
     * @param expireTime
     * @return
     */
    public static String sign(String appCode, Long expireTime) throws Exception {
        if (expireTime == null){
            expireTime = EXPIRE_TIME;
        }
        Date date = new Date(System.currentTimeMillis() + expireTime);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create()
                .withClaim(KEY, appCode)
                .withExpiresAt(date)
                .sign(algorithm);
    }

    /**
     * 验证token
     * @param token
     * @throws Exception
     */
    public static void verify(String token) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        JWTVerifier verifier = JWT.require(algorithm)
                .withClaim(KEY, getUserId(token))
                .build();
        verifier.verify(token);
    }

    /**
     * 获取用户Id
     * @param request
     * @return
     * @throws Exception
     */
    public static String getUserId(HttpServletRequest request) throws Exception {
        String token = getHttpServletRequestToken(request);
        if (token == null || "".equals(token)) {
            throw new Exception("token is null");
        }
        DecodedJWT jwt = JWT.decode(token);
        String userId = jwt.getClaim(KEY).asString();
        return userId;
    }

    /**
     * 获取用户Id
     * @return
     * @throws Exception
     */
    public static String getUserId() throws Exception {
        return getUserId(getHttpServletRequest());
    }

    /**
     * 获取用户id
     * @param token
     * @return
     * @throws Exception
     */
    public static String getUserId(String token) throws Exception {
        if (token == null || "".equals(token)) {
            throw new Exception("token is null");
        }
        DecodedJWT jwt = JWT.decode(token);
        String userId = jwt.getClaim(KEY).asString();
        return userId;
    }

    /**
     * 获取koken值
     * @param request
     * @return
     */
    public static String getToken(ServerHttpRequest request) {
        String token=null;
        if (request.getHeaders().containsKey(CallApiJwtUtil.TOKEN_NAME)){
             token = request.getHeaders().get(CallApiJwtUtil.TOKEN_NAME).get(0);
        }

        if (StringUtils.isEmpty(token)&&request.getCookies().containsKey(TOKEN_NAME)) {
            token = request.getCookies().get(TOKEN_NAME).get(0).toString();

            if (StringUtils.isEmpty(token)&&request.getQueryParams().containsKey(CallApiJwtUtil.TOKEN_NAME)) {
                token = request.getQueryParams().get((CallApiJwtUtil.TOKEN_NAME)).get(0);
            }
        }
        return token;
    }
    public static String getHttpServletRequestToken(HttpServletRequest request) {
        String token = request.getHeader(CallApiJwtUtil.TOKEN_NAME);
        if (StringUtils.isEmpty(token)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (TOKEN_NAME.equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
            if (StringUtils.isEmpty(token)) {
                token = request.getParameter(CallApiJwtUtil.TOKEN_NAME);
            }
        }
        return token;
    }
    /**
     * 获取当前线程请求HttpServletRequest对象
     * @return
     */
    private static HttpServletRequest getHttpServletRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }


}
