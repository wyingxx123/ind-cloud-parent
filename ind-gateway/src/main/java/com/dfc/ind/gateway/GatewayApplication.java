package com.dfc.ind.gateway;



import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



/**
 * 网关启动程序
 *
 * @author admin
 */

@SpringBootApplication
public class GatewayApplication
{
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("============启动成功=============");

    }


}
