package com.dfc.ind.feign;

import com.dfc.ind.common.core.constant.ServiceNameConstants;
import com.dfc.ind.common.core.domain.R;
import com.dfc.ind.entity.sys.SysOperLog;
import com.dfc.ind.feign.fallback.RemoteLogServiceFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 日志服务
 * 
 * @author admin
 */
@FeignClient(value = ServiceNameConstants.SYSTEM_SERVICE, fallback = RemoteLogServiceFallbackImpl.class)
public interface RemoteLogService
{
    /**
     * 保存系统日志
     *
     * @param sysOperLog 日志实体
     * @return 结果
     */
    @PostMapping("/operlog")
    R<Boolean> saveLog(@RequestBody SysOperLog sysOperLog);

    /**
     * 保存访问记录
     *
     * @param username 用户名称
     * @param status 状态
     * @param message 消息
     * @return 结果
     */
    @PostMapping("/logininfor")
    R<Boolean> saveLogininfor(@RequestParam("username") String username, @RequestParam("status") String status,
            @RequestParam("message") String message);
}
