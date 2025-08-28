package com.dfc.ind.gateway.filter;


import com.alibaba.fastjson.JSON;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.redis.service.RedisService;
import com.dfc.ind.gateway.config.AuthIgnoreConfig;
import com.dfc.ind.gateway.entity.RsaVo;
import com.dfc.ind.gateway.entity.TokenInfoVo;
import com.dfc.ind.gateway.mapper.FiltersterMapper;
import com.dfc.ind.gateway.service.FilterService;
import com.dfc.ind.gateway.utils.HttpClientUtil;
import com.dfc.ind.gateway.utils.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 鉴权过滤器
* */

@Component
@Slf4j
public class GlobalFiltes implements GlobalFilter , Order {

    @Resource
    private FilterService filterService;

    @Autowired
    private RedisService redisService;


    @Autowired
    private FiltersterMapper filtersterMapper;

    @Autowired

       AuthIgnoreConfig authIgnoreConfig;


    @Value("${auth.ip:127.0.0.1}")
    private String authIp;

    @Value("${request.time.diff:2}")
    private Long requestTimeDiff;

    @Value("${rsa.priKey:MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAK4QzvU8gHsX0ZJoxCJKitmjJyMU0TnP0XURjaWOFSw3k1WWfS/eKE433BFLhxdG8rfrE7ql1bUETWh1RjK9PFoio/3wpLzdS7ZoboP4ITGTy4zSDeZwcCtXE/fsKTiy1wPnBOuEHPCdt2lu4yoi0kS17UvzfO5g0ardwk2LdA9/AgMBAAECgYBRRJu7t8GsttQr7SoVcIQfVKNDJ8b/nN2IMOfXMd0ExfXN8fME1E4xJrdig8bQwVk1MVYGwMJkP1v8tzRNIDj6fqKe9Ewa1SKKdT63fEMzSn0Pa43ppNjJWr3oIzciLxIxPKjjtKPh8nBAAuupq49jgfP22S6JumlIQ342yw/kMQJBAOOGSEhhjUHHfhrvpwF3H/Ugai11tEuQB7S+NlH/nwZuVdEnkG735PhptzqvZ2R2tGbU1A7ajWUoA7TbO/OGP+UCQQDD2b470q4EDbPg69b/k8z/9/l9tlsM19rINU2qbuWscHENdq2l0pz8phOhjkx2spVvT65Q/IM2CT5e2KAqa/OTAkBwId7/5SwD7jilN9U78KTMX1RU4TyhPPO/TTtiQDP0rG4Y7YHOXtf24csO3iF7rtEMGPoF9ApZf1YMTTwHsfNNAkBKcmqtstgTEmJeDUgcvsIeStS7xKW3rBWuJRTwxFbpxZQz2fkIH5ctMrQjpUPLmvbS6ScKAfKeh8T9qLq5ZW+hAkARefA/jJt1tcZpBYUCNzxX8dbNYkH5nmIaO4kfKkLkiIObltqlSvTVXsHiazCCCXk1iY+x9kRz184tmPbUBKYf}")
    private String priKey;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        //用户ID
        String userId = null;
        String merchantId = null;
        String token = null;
        log.info("当前请求" + exchange.getRequest().getURI().getPath() + "这样的");
        String path = exchange.getRequest().getURI().getPath();
        
        // 添加调试日志
        log.info("authIgnoreConfig.getUrls(): " + authIgnoreConfig.getUrls());
        
        //如果是免认证接口直接放行
        for (String url : authIgnoreConfig.getUrls()) {
            url=url.replaceAll("\\*",".*");
            log.info("检查路径匹配: " + path + " vs " + url);
            if (path.matches(url)){
                log.info("路径匹配成功，直接放行: " + path);
                return chain.filter(exchange);
            }
        }
        if (path.equals("/oauth/token")||path.equals("/dpub/authorize")||path.equals("/oauth/check_token")||path.equals("/user/getInfo")){
            return chain.filter(exchange);
        }
        // Swagger 相关路径直接放行
        if (path.equals("/doc.html") || path.startsWith("/swagger-resources") || 
            path.startsWith("/v2/api-docs") || path.startsWith("/webjars/") ||
            path.startsWith("/system/v2/api-docs") || path.startsWith("/api/v2/api-docs")) {
            return chain.filter(exchange);
        }
        ServerHttpResponse response = exchange.getResponse();
        String ip = getIp(request, request.getHeaders());
        //攻击拦截
        String encodedSecret = request.getHeaders().getFirst("SignCode");
        if (StringUtils.isEmpty(encodedSecret)){
            DataBuffer wrap =writeRes(exchange, "签名不能为空", response, ip,  request.getPath());
            return response.writeWith(Mono.just(wrap));
        }
        try {
            String deStr=  RSAUtils.decrypt(encodedSecret,priKey);
            RsaVo rsaVo = JSON.parseObject(deStr, RsaVo.class);
            long l = System.currentTimeMillis();
            Long requestTime = rsaVo.getRequestTime();
            if ((l-requestTime)/1000>requestTimeDiff){
                DataBuffer wrap =writeRes(exchange, "超时请求不予通过", response, ip,  request.getPath());
                return response.writeWith(Mono.just(wrap));
            }

        }catch (Exception e){
            DataBuffer wrap =writeRes(exchange, "解析ENCODED_SECRET失败", response, ip,  request.getPath());
            return response.writeWith(Mono.just(wrap));

        }

        //获取请求携带的token
        String authorization = request.getHeaders().getFirst("Authorization");
        if (authorization != null) {
            token = authorization.replace("Bearer ", "");
           String tokenInfo= redisService.getCacheObject(token);
           if (StringUtils.isNotEmpty(tokenInfo)){
               TokenInfoVo tokenInfoVo = JSON.parseObject(tokenInfo, TokenInfoVo.class);
               userId =tokenInfoVo.getUser_id();
               merchantId = tokenInfoVo.getM_id().toString();
           }else {
               String url = "http://" + authIp + ":8604/ind-auth/oauth/check_token?token=" + token;
               try {
                   log.info("url============"+url);
                   String s = HttpClientUtil.doPost(url,null);
                   if (org.apache.commons.lang3.StringUtils.isNotBlank(s) && s.contains("user_id")) {
                       TokenInfoVo tokenInfoVo = JSON.parseObject(s, TokenInfoVo.class);
                       userId =tokenInfoVo.getUser_id();
                       merchantId = tokenInfoVo.getM_id().toString();
                       redisService.setCacheObject(token,JSON.toJSONString(tokenInfoVo));
                       redisService.expire(token,30, TimeUnit.MINUTES);
                   }else {
                       DataBuffer wrap =writeRes(exchange, "token校验失败", response, ip,  request.getPath());
                       return response.writeWith(Mono.just(wrap));
                   }
               } catch (Exception e) {
                   DataBuffer wrap =writeRes(exchange, "token信息获取失败", response, ip,  request.getPath());
                   return response.writeWith(Mono.just(wrap));
               }
           }


        }

        //url 路径
      /*  Integer YorN = filterService.selectByurlName(path);
        if (path.equals("/menu/getRouters")) {
            redisService.setCacheObject(userId, token);
            redisService.expire(userId, 43200);
        }
        if (path.equals("/token/logout")) {
            redisService.deleteObject(userId);
        }*/
      /*  if (YorN == 0) {
            if (exchange.getRequest().getCookies().size() > 0) {
                if (YorN == 0) {
                    //判断是否为小程序路径
                    log.info("type={}", exchange.getRequest().getHeaders().get("type"));
                    if (exchange.getRequest().getHeaders().get("type").toString().substring(1, exchange.getRequest().getHeaders().get("type").toString().length() - 1).equals("01")) {
                        List<String> roleId = filterService.selectByUserId(userId, merchantId);
                        log.info("roleId={}", roleId);
                        //根据路径和用户角色查看是否有权限
                        log.info("/" + path.split("/")[1]);
                        //判断路径首位是否为数字（判断几种情况 1 首位为数字 2 中间为数zi）
                        if (StringUtils.isNumeric(path.split("/")[1])) {
                            path = path.split("/")[2];
                        } else {
                            path = path.split("/")[1];
                        }
                        int YON = 0;
                        for (int i = 0; i < roleId.size(); i++) {
                            YON += filterService.selectByPathAndRoleId("/" + path, roleId.get(i));
                        }
                        //有权限通行 无权限返回
                        //0 =无权限  1= 有权限
                        if (YON == 0) {
                            int ys = 0;
                            for (int j = 0; j < roleId.size(); j++) {
                                List<String> muenname = filterService.selectByroldormuliscount(roleId.get(j), merchantId, path);
                                for (int i = 0; i < muenname.size(); i++) {
                                    int yno = filterService.selectBymunename(muenname.get(i), roleId.get(j), merchantId);
                                    ys+=yno;
                                }
                            }
                            //角色菜单表查询是否与权限（0 == 没权限 1= 有权限）
                            if (ys == 0) {
                                DataBuffer wrap =writeRes(exchange, "当前请求" + path + "无权限", response, ip,  request.getPath());
                                return response.writeWith(Mono.just(wrap));
                            }
                        }
                    }
                    //00 小程序
                    if (exchange.getRequest().getHeaders().get("type").toString().substring(1, exchange.getRequest().getHeaders().get("type").toString().length() - 1).equals("00")) {

                        if (StringUtils.isNumeric(path.split("/")[1])) {
                            path = path.split("/")[2];
                        } else {
                            path = path.split("/")[1];
                        }
                        int yon = filterService.selectBytypeorroleId(exchange.getRequest().getHeaders().get("type").toString().substring(1, exchange.getRequest().getHeaders().get("type").toString().length() - 1), "1", path);
                        if (yon == 0) {
                            DataBuffer wrap =writeRes(exchange, "当前请求" + path + "无权限", response, ip,  request.getPath());
                            return response.writeWith(Mono.just(wrap));
                        }
                    }
                }
            }
        }*/
        return chain.filter(exchange);
    }

    @Override
    public int value() {
        return 1;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
    private static DataBuffer writeRes(ServerWebExchange exchange, String msg, ServerHttpResponse response, String ip, RequestPath reqPath) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        JsonResults data = JsonResults.noAuth(msg);
        DataBuffer wrap = exchange.getResponse().bufferFactory().wrap(JSON.toJSONString(data).getBytes(StandardCharsets.UTF_8));
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        log.info("{}:{}:{}", ip, reqPath, msg);
        return wrap;
    }
    private static String getIp(ServerHttpRequest req, HttpHeaders headers) {
        String ip;
        ip = CollectionUtils.isEmpty(headers.get("X-Forwarded-For")) ? null : headers.get("X-Forwarded-For").get(0);
        if (ip == null) {
            ip = CollectionUtils.isEmpty(headers.get("X-Real-IP")) ? null : headers.get("X-Real-IP").get(0);
        }
        if (ip == null) {
            ip = CollectionUtils.isEmpty(headers.get("Proxy-Client-IP")) ? null : headers.get("Proxy-Client-IP").get(0);
        }
        if (ip == null) {
            ip = CollectionUtils.isEmpty(headers.get("WL-Proxy-Client-IP")) ? null : headers.get("WL-Proxy-Client-IP").get(0);
        }

        if (ip == null) {
            ip = CollectionUtils.isEmpty(headers.get("HTTP_X_FORWARDED_FOR")) ? null : headers.get("HTTP_X_FORWARDED_FOR").get(0);
        }

        if (ip == null) {
            ip = req.getRemoteAddress().getHostString();
        }
        return ip;
    }

}
