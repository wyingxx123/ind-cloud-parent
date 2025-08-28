package com.dfc.ind.common.security.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import com.dfc.ind.common.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 导入 SecurityImportBeanDefinitionRegistrar 自动加载类
 * 
 * @author admin
 */
public class SecurityImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar
{
    private static final Logger logger = LoggerFactory.getLogger(SecurityImportBeanDefinitionRegistrar.class);
    
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry)
    {
        logger.info("=== SecurityImportBeanDefinitionRegistrar.registerBeanDefinitions() called! ===");
        Class<ResourceServerConfig> aClass = ResourceServerConfig.class;
        String beanName = StringUtils.uncapitalize(aClass.getSimpleName());
        logger.info("Registering ResourceServerConfig with bean name: {}", beanName);
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ResourceServerConfig.class);
        registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        logger.info("=== ResourceServerConfig registered successfully! ===");
    }
}
