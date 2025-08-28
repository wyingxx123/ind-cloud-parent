package com.dfc.ind.common.security.config;

import com.dfc.ind.common.security.handler.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * oauth2 服务配置 (处理资源服务器的Token)
 * 这里注入oauth2相关的config
 *
 * 为什么认证服务器也需要变成资源服务器：
 * 因为oauth2在授权码模式下 oauth客户端在在获取access_token后 会从后台调用授权服务器的用户信息端点来构建认证对象（Authentication）
 * 因为用户信息端点需要被资源服务器保护起来 所以需要将授权服务器同时配置为资源服务器
 *
 * 另外
 *
 * 资源服务器的职责是对来自oauth客户端的access_token进行鉴权 一个资源服务器包含多个端点（接口） 一部分端点作为资源服务器的资源
 * 提供给oauth的client访问 另一部分端点不由资源服务器管理 由资源服务器管理的端点安全性配置在此类中
 * 其余端点安全性配置在SecurityConfiguration类中
 * 当请求中包含OAuth2 access_token时，Spring Security会根据资源服务器配置进行过滤
 * EnableResourceServer会创建一个WebSecurityConfigurerAdapter 执行顺序是 3
 * 在SecurityConfiguration类之前执行 优先级更高
 *
 * 用户携带令牌去访问网关 -> 网关转发到 -> 资源服务器 ->资源服务器携带token去访问认证服务器
 * -> 认证服务器调用tokenstore校验token合法性（这个过程check_token） -> 认证服务器返回资源服务器当前用户的身份和权限等
 *
 *
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter
{
    private static final Logger logger = LoggerFactory.getLogger(ResourceServerConfig.class);
    @Autowired
    private ResourceServerProperties resourceServerProperties;

    @Autowired
    private OAuth2ClientProperties oAuth2ClientProperties;

    @Autowired
    private AuthIgnoreConfig authIgnoreConfig;

    @PostConstruct
    public void init() {
        logger.info("=== ResourceServerConfig @PostConstruct called! ===");
        logger.info("=== @EnableResourceServer annotation should activate this config ===");
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate()
    {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

    @Primary
    @Bean
    public ResourceServerTokenServices tokenServices()
    {
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        UserAuthenticationConverter userTokenConverter = new CommonUserConverter();
        accessTokenConverter.setUserTokenConverter(userTokenConverter);
        remoteTokenServices.setCheckTokenEndpointUrl(resourceServerProperties.getTokenInfoUri());
        remoteTokenServices.setClientId(oAuth2ClientProperties.getClientId());
        remoteTokenServices.setClientSecret(oAuth2ClientProperties.getClientSecret());
        remoteTokenServices.setRestTemplate(restTemplate());
        remoteTokenServices.setAccessTokenConverter(accessTokenConverter);
        return remoteTokenServices;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception
    {
        logger.info("=== ResourceServerConfig.configure() called! ===");
        http.csrf().disable();
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http
                .authorizeRequests();
        // 不登录可以访问
        logger.info("AuthIgnoreConfig URLs: " + authIgnoreConfig.getUrls());
        authIgnoreConfig.getUrls().forEach(url -> {
            logger.info("Adding permit all for URL: " + url);
            registry.antMatchers(url).permitAll();
        });
        
        // 临时添加测试端点
        registry.antMatchers("/test/**").permitAll();
        logger.info("Added permitAll for /test/**");
        
        // 添加 Swagger 相关路径
        registry.antMatchers("/v2/api-docs").permitAll();
        registry.antMatchers("/swagger-resources/**").permitAll();
        registry.antMatchers("/swagger-ui.html").permitAll();
        registry.antMatchers("/doc.html").permitAll();
        registry.antMatchers("/webjars/**").permitAll();
        logger.info("Added permitAll for Swagger paths");
        registry.anyRequest().authenticated();
        logger.info("=== ResourceServerConfig.configure() completed! ===");
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources)
    {
        resources
                .tokenServices(tokenServices())
                //这里把自定义异常加进去
               .authenticationEntryPoint(new AuthExceptionEntryPoint())
               .accessDeniedHandler(new CustomAccessDeniedHandler());
    }
}
