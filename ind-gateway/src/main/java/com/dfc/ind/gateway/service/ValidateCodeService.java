package com.dfc.ind.gateway.service;

import com.dfc.ind.common.core.exception.CaptchaException;
import com.dfc.ind.common.core.web.domain.JsonResults;

import java.io.IOException;

/**
 * 验证码处理
 * 
 * @author admin
 */
public interface ValidateCodeService
{
    /**
     * 生成验证码
     */
    public JsonResults createCapcha() throws IOException, CaptchaException;

    /**
     * 校验验证码
     */
    public void checkCapcha(String key, String value) throws CaptchaException;
}
