package com.dfc.ind.auth;

import com.dfc.ind.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * 认证授权中心
 *
 * @author admin
 */
@EnableDiscoveryClient
@EnableRyFeignClients
@SpringBootApplication
public class AuthApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(AuthApplication.class, args);
    }
}
