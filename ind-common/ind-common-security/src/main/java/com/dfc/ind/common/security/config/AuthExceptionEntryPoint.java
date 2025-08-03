package com.dfc.ind.common.security.config;

import com.alibaba.fastjson.JSON;
import com.dfc.ind.common.core.constant.HttpStatus;
import com.dfc.ind.common.core.utils.ServletUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 描述:无效token 异常重写
 * </p>
 *
 * @author wenfl 温发良
 * @date 2020-03-31
 * @copyright 武汉数慧享智能科技有限公司
 */
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws ServletException {
        JsonResults results;
        Throwable cause = authException.getCause();
        if(cause instanceof InvalidTokenException) {
            results=JsonResults.error(HttpStatus.UNAUTHORIZED,"无效的token");
        }else{
            results=JsonResults.error(HttpStatus.FORBIDDEN,"无权限访问");
        }
        ServletUtils.renderString(response, JSON.toJSONString(results));
    }
}
