package com.dfc.ind.common.security.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.dfc.ind.common.core.constant.HttpStatus;
import com.dfc.ind.common.core.exception.BaseException;
import com.dfc.ind.common.core.exception.CustomException;
import com.dfc.ind.common.core.exception.DemoModeException;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;

/**
 * 全局异常处理器
 * 
 * @author admin
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 基础异常
     */
    @ExceptionHandler(BaseException.class)
    public JsonResults baseException(BaseException e)
    {
        return JsonResults.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(CustomException.class)
    public JsonResults businessException(CustomException e)
    {
        if (StringUtils.isNull(e.getCode()))
        {
            return JsonResults.error(e.getMessage());
        }
        return JsonResults.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public JsonResults handlerNoFoundException(Exception e)
    {
        log.error(e.getMessage(), e);
        return JsonResults.error(HttpStatus.NOT_FOUND, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public JsonResults handleAuthorizationException(AccessDeniedException e)
    {
        log.error(e.getMessage());
        return JsonResults.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
    }

    @ExceptionHandler(AccountExpiredException.class)
    public JsonResults handleAccountExpiredException(AccountExpiredException e)
    {
        log.error(e.getMessage(), e);
        return JsonResults.error(e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public JsonResults handleUsernameNotFoundException(UsernameNotFoundException e)
    {
        log.error(e.getMessage(), e);
        return JsonResults.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public JsonResults handleException(Exception e)
    {
        log.error(e.getMessage(), e);
        return JsonResults.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public JsonResults validatedBindException(BindException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return JsonResults.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validExceptionHandler(MethodArgumentNotValidException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return JsonResults.error(message);
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public JsonResults demoModeException(DemoModeException e)
    {
        return JsonResults.error("演示模式，不允许操作");
    }
}
