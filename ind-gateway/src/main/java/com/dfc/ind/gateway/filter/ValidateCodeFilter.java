package com.dfc.ind.gateway.filter;

import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.gateway.service.ValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import reactor.core.publisher.Mono;

/**
 * 验证码和短信验证码过滤器
 *
 * @author admin
 */
@Component
public class ValidateCodeFilter extends AbstractGatewayFilterFactory<Object>
{
    private final static String AUTH_URL = "/oauth/token";

    //private final static String AUTH_URLAPP = "/oauth/token/getWechatToken";


    @Autowired
    private ValidateCodeService validateCodeService;

    private static final String BASIC_ = "Basic ";

    private static final String CODE = "code";

    private static final String UUID = "uuid";

    private static final String GRANT_TYPE = "grant_type";

    private static final String REFRESH_TOKEN = "refresh_token";

    @Override
    public GatewayFilter apply(Object config)
    {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 非登录请求，不处理
            if (!StringUtils.containsIgnoreCase(request.getURI().getPath(), AUTH_URL))
            {
                return chain.filter(exchange);
            }

            /*if (StringUtils.equals(request.getURI().getPath(), AUTH_URLAPP))
            {
                return chain.filter(exchange);
            }*/

            // 刷新token请求，不处理
            String grantType = request.getQueryParams().getFirst(GRANT_TYPE);
            if (StringUtils.containsIgnoreCase(request.getURI().getPath(), AUTH_URL) && StringUtils.containsIgnoreCase(grantType, REFRESH_TOKEN))
            {
                return chain.filter(exchange);
            }
            //

            // 消息头存在内容，且不存在验证码参数，不处理
            String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (StringUtils.isNotEmpty(header) && StringUtils.startsWith(header, BASIC_)
                    && !request.getQueryParams().containsKey(CODE) && !request.getQueryParams().containsKey(UUID))
            {
                return chain.filter(exchange);
            }
            String loginType = request.getQueryParams().getFirst("loginType");
            if (StringUtils.isNotEmpty(loginType)&&"sms".equals(loginType)){
                return chain.filter(exchange);
            }
            try
            {
                validateCodeService.checkCapcha(request.getQueryParams().getFirst(CODE),
                        request.getQueryParams().getFirst(UUID));
            }
            catch (Exception e)
            {
                ServerHttpResponse response = exchange.getResponse();
                response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
                return exchange.getResponse().writeWith(
                        Mono.just(response.bufferFactory().wrap(JSON.toJSONBytes(JsonResults.error(e.getMessage())))));
            }
            return chain.filter(exchange);
        };
    }
}
