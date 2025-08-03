package com.dfc.ind.gateway.filter;


import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.utils.secret.AESUtil;
import com.dfc.ind.common.core.utils.secret.NanShaAESUtil;
import com.dfc.ind.common.core.utils.secret.RSAUtils;
import com.dfc.ind.common.core.utils.sign.CallApiJwtUtil;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.redis.service.RedisService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 鉴权过滤器
 */

@Component
@Slf4j
public class CallApiFilter implements GlobalFilter, Order {
    @Autowired
    private RedisService redisService;
    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();
    private final static String callApiPath = "/dpub/apiCall/";
    private static final String CONTENT_TYPE = "application/json";
    @Value("${pub.key}")
    private String pubKey;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //url权限校验开关 默认关闭
        final boolean url_check_flag;
        final String decodingFlag;
        String path = exchange.getRequest().getURI().getPath();
        if (!path.contains(callApiPath)) {
            return chain.filter(exchange);
        }
        log.info("当前请求:"+path);
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest req = exchange.getRequest();
        RequestPath reqPath = req.getPath();
        String token = CallApiJwtUtil.getToken(req);
        // 获取IPv4地址
        final String ip;
        HttpHeaders headers = req.getHeaders();
        // 获取真实IPv4地址
        ip = getIp(req, headers);

        if (token == null) {
            DataBuffer wrap = writeRes(exchange, "请求头未携带access-token", response, ip, reqPath);
            return response.writeWith(Mono.just(wrap));
        }
        try {
            CallApiJwtUtil.verify(token);
        } catch (Exception e) {
            DataBuffer wrap = writeRes(exchange, "无效授权", response, ip, reqPath);
            return response.writeWith(Mono.just(wrap));

        }
        String url_check_flag_str = redisService.getCacheObject("sys_config:url_check_flag");
        if (StringUtils.isNotEmpty(url_check_flag_str) && "1".equals(url_check_flag_str)) {
            url_check_flag = true;
        } else {
            url_check_flag = false;
        }

        decodingFlag = redisService.getCacheObject("sys_config:decode_flag");
        final String aesKey;
        if ("1".equals(decodingFlag)) {
            //解密
            String encodedAesSecret = headers.getFirst("ENCODED_AES_SECRET");

            if (StringUtils.isEmpty(encodedAesSecret)) {
                DataBuffer wrap = writeRes(exchange, "ENCODED_AES_SECRET不能为空", response, ip, reqPath);
                return response.writeWith(Mono.just(wrap));
            }
            AtomicReference<String> requestBody = new AtomicReference<>("");
            try {
                byte[] aesSecret = RSAUtils.decryptByPublicKey(Base64.getDecoder().decode(encodedAesSecret), pubKey);
                aesKey = new String(Base64.getDecoder().decode(aesSecret), "UTF-8");
                ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);
                Mono<String> modifiedBody = serverRequest.bodyToMono(String.class)
                        .flatMap(body -> {
                            JSONObject jsonObject = JSON.parseObject(body);
                            String decrypt = NanShaAESUtil.decrypt(jsonObject.get("data").toString(), aesKey);
                            //设置requestBody到变量，让response获取
                            requestBody.set(decrypt);
                            log.info("requestBody ============ {}", decrypt);
                            return Mono.just(decrypt);
                        });
                // 通过 BodyInserter 插入 body(支持修改body), 避免 request body 只能获取一次
                BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
                HttpHeaders headerss = new HttpHeaders();
                headerss.putAll(exchange.getRequest().getHeaders());
                headerss.remove("Content-Length");
                CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headerss);
                DataBuffer buffer = checkAuth(url_check_flag, decodingFlag, req, aesKey, response, exchange, ip, reqPath, path);
                if (buffer != null) {
                    return response.writeWith(Mono.just(buffer));
                }
                return bodyInserter.insert(outputMessage, new BodyInserterContext())
                        .then(Mono.defer(() -> {
                            // 重新封装请求
                            ServerHttpRequest decoratedRequest = requestDecorate(exchange, headerss, outputMessage);
                            // 记录普通的
                            return chain.filter(exchange.mutate().request(decoratedRequest).build()) ;
                        }));
            } catch (Exception e) {
                log.error("解密失败", e);
                DataBuffer wrap = writeRes(exchange, "解密失败", response, ip, reqPath);
                return response.writeWith(Mono.just(wrap));
            }
        } else {
            aesKey = "";
        }

        DataBuffer dataBuffer = checkAuth(url_check_flag, decodingFlag, req, aesKey, response, exchange, ip, reqPath, path);
        if (dataBuffer != null) {
            return response.writeWith(Mono.just(dataBuffer));
        }
        return chain.filter(exchange);
    }

    /**
     * 记录响应日志
     * 通过 DataBufferFactory 解决响应体分段传输问题。
     */
    private ServerHttpResponseDecorator recordResponseLog(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();

        return new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    // 获取响应类型，如果是 json 就打印
                    String originalResponseContentType = exchange.getAttribute(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);

                    if (Objects.equals(this.getStatusCode(), HttpStatus.OK)
                            && org.apache.commons.lang3.StringUtils.isNotBlank(originalResponseContentType)
                            && originalResponseContentType.contains(CONTENT_TYPE)) {

                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            // 合并多个流集合，解决返回体分段传输
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer join = dataBufferFactory.join(dataBuffers);
                            byte[] content = new byte[join.readableByteCount()];
                            join.read(content);
                            // 释放掉内存
                            DataBufferUtils.release(join);
                            return bufferFactory.wrap(content);
                        }));
                    }
                }
                return super.writeWith(body);
            }
        };
    }

    private ServerHttpRequestDecorator requestDecorate(ServerWebExchange exchange, HttpHeaders headers,
                                                       CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    // TODO: this causes a 'HTTP/1.1 411 Length Required' // on
                    // httpbin.org
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }

    private DataBuffer checkAuth(Boolean url_check_flag, String decodingFlag, ServerHttpRequest req, String aesKey, ServerHttpResponse response
            , ServerWebExchange exchange, String ip, RequestPath reqPath, String path
    ) {
        if (url_check_flag) {
            String user_id = null;
            //校验api权限
            if ("1".equals(decodingFlag)) {
                String encodedUserId = req.getHeaders().getFirst("USER_ID");
                if (StringUtils.isEmpty(aesKey)) {
                    return writeRes(exchange, "获取秘钥失败", response, ip, reqPath);
                }
                user_id = AESUtil.decrypt(encodedUserId, aesKey);
            } else {
                user_id = req.getHeaders().getFirst("USER_ID");
            }
            if (StringUtils.isEmpty(user_id)) {
                return writeRes(exchange, "USER_ID不能为空", response, ip, reqPath);

            }

            List<String> roleIdList = getUserRole(user_id);
            log.info("{}:{}:{},用户:{},角色:{}", ip, reqPath, "api权限校验", user_id, roleIdList);
            req = exchange.getRequest().mutate().header("USER_ID", user_id).build();
            exchange = exchange.mutate().request(req).build();

            //校验通道角色
            //目标微服务
            String serverUrl = "ind-api";
            String apiPath = path.split(callApiPath)[1];
            String channer_type = null;
            List<String> channerList = req.getQueryParams().get("CHANNER_TYPE");
            if (!CollectionUtils.isEmpty(channerList)) {
                channer_type = channerList.get(0);
            }
            try {
                //校验白名单
                if (checkWhiteIplist(ip)) {
                    return null;
                }
            } catch (Exception e) {
                log.error("ip白名单校验失败", e);
                return writeRes(exchange, "ip白名单校验失败", response, ip, reqPath);

            }
            if (CollectionUtils.isEmpty(roleIdList)) {
                log.error("查询不到用户{}的角色信息", user_id);
                return writeRes(exchange, "查询不到用户" + user_id + "的角色信息", response, ip, reqPath);

            }
            try {
                if (checkChanner(serverUrl,channer_type,roleIdList)){
                    return null;
                }
            }catch (Exception e){
                log.error("通道校验失败",e);
                return writeRes(exchange, "通道校验失败", response, ip, reqPath);
            }

            try {
                if (!checkApiRole(serverUrl,roleIdList,apiPath)) {
                    return writeRes(exchange, "用户无权限访问", response, ip, reqPath);

                }
            }catch (Exception e){
                log.error("api权限校验失败",e);
                return writeRes(exchange, "api权限校验失败", response, ip, reqPath);
            }
        }
        return null;
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

    /**
     * 查询用户角色
     * @param user_id 用户id
     * @return
     */
    private List<String> getUserRole(String user_id) {
        List<String> roleIdList=new ArrayList<>(5);
        String apiKey="ind-cloud-sys:secu_requsr_fromzsh:"+user_id+":";

        Collection<String> keys = redisService.keys(apiKey+"*");
        if (!CollectionUtils.isEmpty(keys)){
            for (String key : keys) {
               String roleId= redisService.getCacheMapValue(key,"role_id");
                roleIdList.add(roleId);
            }
        }
        return roleIdList;
    }

    private static DataBuffer writeRes(ServerWebExchange exchange, String msg, ServerHttpResponse response, String ip, RequestPath reqPath) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        JsonResults data = JsonResults.success(msg);
        DataBuffer wrap = exchange.getResponse().bufferFactory().wrap(JSON.toJSONString(data).getBytes(StandardCharsets.UTF_8));
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        log.info("{}:{}:{}", ip, reqPath, msg);
        return wrap;
    }

    private boolean checkApiRole(String serverUrl, List<String> roleIdList, String apiPath) throws ParseException {
        for (String roleId : roleIdList) {
            String apiKey="ind-cloud-sys:secu_api_empower:"+serverUrl+":"+apiPath;
            Map<String,Object>  apiEmpowerInfo= redisService.getCacheMap(apiKey);
            if (!CollectionUtils.isEmpty(apiEmpowerInfo)){
               String roleIdStr= apiEmpowerInfo.get("role_id").toString();
                for (String roleIdS : roleIdStr.split(",")) {
                    if (!roleId.equals(roleIdS)){
                        continue;
                    }
                    if (checkEffectTime(apiEmpowerInfo)){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkChanner(String serverUrl, String channerType, List<String> roleIdList) throws ParseException {

        String channerKey="ind-cloud-sys:secu_channer_empower:"+serverUrl+":"+channerType;
        Map<String,Object>  channerInfo= redisService.getCacheMap(channerKey);
        if (!CollectionUtils.isEmpty(channerInfo)){
            String channer_role_id = channerInfo.get("channer_role_id").toString();
            for (String roleId : roleIdList) {
                for (String roleIdS : channer_role_id.split(",")) {
                   if (!roleId.equals(roleIdS)){
                       continue;
                   }
                    if (checkEffectTime(channerInfo)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean checkWhiteIplist( String ip) throws ParseException {
        String key="ind-cloud-sys:"+"secu_power_white_list:"+ip;
        Map<String, Object> cacheMap = redisService.getCacheMap(key);
        if (!CollectionUtils.isEmpty(cacheMap)){
            //有效开始时间
            if (checkEffectTime(cacheMap)) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkEffectTime( Map<String, Object> cacheMap) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS);
        if (cacheMap.containsKey("take_effect_time")){
            Date date = DateUtils.dateTime(DateUtils.YYYY_MM_DD, DateUtils.getDate());
            String startTimeStr = cacheMap.get("take_effect_time").toString().replace("\\x00", " ");
            Date startTime = format.parse(startTimeStr);
            if (startTime.getTime()<=date.getTime()){
                //有效结束时间
                if (cacheMap.containsKey("lose_effect_time")){
                    String endTimeStr = cacheMap.get("lose_effect_time").toString().replace("\\x00", " ");
                    Date endTime = format.parse(endTimeStr);
                    if (endTime.getTime()>=date.getTime()){
                        //白名单通过
                        return true;
                    }else {
                        return false;
                    }
                }

            }else {
              return false;
            }
        }
        return true;
    }

    @Override
    public int value() {
        return -22;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }


}
