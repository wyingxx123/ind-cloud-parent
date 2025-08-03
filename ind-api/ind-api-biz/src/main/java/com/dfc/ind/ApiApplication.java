package com.dfc.ind;

import com.dfc.ind.common.security.annotation.EnableRyFeignClients;
import com.dfc.ind.common.swagger.annotation.EnableCustomSwagger2;
import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * <p>
 * 描述: dataAPI接口服务启动类
 * </p>
 *
 */

@EnableRyFeignClients
@EnableCustomSwagger2
@SpringBootApplication
@MapperScan({"com.dfc.ind.mapper","com.dfc.ind.mapper.*"})
@EnableMPP
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
