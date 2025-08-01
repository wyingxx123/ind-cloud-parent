package com.dfc.ind.feign.fallback;

import com.dfc.ind.common.core.domain.R;
import com.dfc.ind.entity.sys.SysOperLog;
import com.dfc.ind.feign.RemoteLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteLogServiceFallbackImpl implements RemoteLogService {
    @Override
    public R<Boolean> saveLog(SysOperLog sysOperLog) {
        log.error("日志信息失败，sysOperLog >> {}", sysOperLog);
        return null;
    }

    @Override
    public R<Boolean> saveLogininfor(String username, String status, String message) {
        log.error("保存访问信息失败，username >> {}，status >> {}，message >> {}", username,status,message);
        return null;
    }
}
