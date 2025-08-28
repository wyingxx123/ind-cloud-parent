package com.dfc.ind.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Swagger 安全配置
 * 优先级高于 ResourceServerConfig，专门处理 Swagger 相关路径的访问
 */
@Configuration
@Order(1)
public class SwaggerSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .requestMatchers()
            .antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/doc.html", "/webjars/**")
            .and()
            .authorizeRequests()
            .anyRequest().permitAll()
            .and()
            .csrf().disable();
    }
}