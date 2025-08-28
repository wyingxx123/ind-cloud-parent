package com.dfc.ind.system.controller;

import com.dfc.ind.common.security.config.AuthIgnoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private AuthIgnoreConfig authIgnoreConfig;

    @GetMapping("/auth-ignore")
    public Map<String, Object> getAuthIgnoreConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("urls", authIgnoreConfig.getUrls());
        result.put("size", authIgnoreConfig.getUrls().size());
        return result;
    }
}