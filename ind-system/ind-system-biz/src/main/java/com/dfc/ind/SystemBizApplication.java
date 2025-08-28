package com.dfc.ind;

import com.dfc.ind.common.security.annotation.EnableCustomConfig;
import com.dfc.ind.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 系统管理启动类
 *
 * @author wenfl
 */
@EnableCustomConfig
@EnableRyFeignClients
@EnableResourceServer
@SpringBootApplication
public class SystemBizApplication {
    private static final Logger logger = LoggerFactory.getLogger(SystemBizApplication.class);
    
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SystemBizApplication.class, args);
        
        // 检查 ResourceServerConfig 是否被注册
        boolean hasResourceServerConfig = context.containsBean("resourceServerConfig");
        logger.info("=== ResourceServerConfig bean exists: {} ===", hasResourceServerConfig);
        
        if (hasResourceServerConfig) {
            Object bean = context.getBean("resourceServerConfig");
            logger.info("=== ResourceServerConfig bean class: {} ===", bean.getClass().getName());
        }
    }
}
