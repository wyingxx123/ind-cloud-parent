package com.dfc.ind.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Swagger 安全配置
 * 允许 Swagger 相关路径无需认证访问
 *
 * @author admin
 */
@Configuration
@EnableWebFluxSecurity
@Order(1)
public class SwaggerSecurityConfig {

    @Bean
    public SecurityWebFilterChain swaggerSecurityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                    .pathMatchers("/doc.html", "/swagger-resources/**", "/v2/api-docs", "/webjars/**", 
                                 "/system/v2/api-docs", "/api/v2/api-docs").permitAll()
                    .anyExchange().authenticated()
                )
                .csrf().disable()
                .build();
    }
}