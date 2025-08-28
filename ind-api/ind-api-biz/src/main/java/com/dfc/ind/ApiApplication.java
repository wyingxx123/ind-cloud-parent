package com.dfc.ind;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * <p>
 * 描述: dataAPI接口服务启动类
 * </p>
 *
 */

@EnableFeignClients
@SpringBootApplication
@MapperScan({"com.dfc.ind.mapper","com.dfc.ind.mapper.*"})
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
