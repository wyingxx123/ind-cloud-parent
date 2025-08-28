package com.dfc.ind.gateway;



import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



/**
 * 网关启动程序
 *
 * @author admin
 */

@SpringBootApplication(exclude = {
    com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration.class,
    com.alibaba.cloud.nacos.NacosConfigAutoConfiguration.class,
    com.alibaba.cloud.nacos.discovery.NacosDiscoveryClientConfiguration.class,
    com.alibaba.cloud.nacos.discovery.reactive.NacosReactiveDiscoveryClientConfiguration.class,
    com.alibaba.cloud.nacos.registry.NacosServiceRegistryAutoConfiguration.class
})
@MapperScan("com.dfc.ind.gateway.mapper")
public class GatewayApplication
{
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("============启动成功=============");

    }


}
