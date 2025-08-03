package com.dfc.ind;

import com.dfc.ind.common.security.annotation.EnableCustomConfig;
import com.dfc.ind.common.security.annotation.EnableRyFeignClients;
import com.dfc.ind.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 系统管理启动类
 *
 * @author wenfl
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication
public class SystemBizApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemBizApplication.class, args);
    }
}
