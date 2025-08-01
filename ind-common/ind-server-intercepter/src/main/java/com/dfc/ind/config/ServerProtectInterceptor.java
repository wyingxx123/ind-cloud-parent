package com.dfc.ind.config;


import com.alibaba.fastjson.JSON;
import com.dfc.ind.common.core.utils.ServletUtils;
import com.dfc.ind.common.core.utils.common.Constant;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.domain.LoginUser;
import com.dfc.ind.common.security.utils.SecurityUtils;
import lombok.NonNull;
import org.apache.catalina.security.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.servlet.HandlerInterceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServerProtectInterceptor implements HandlerInterceptor {

   /* @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler){

        String token = request.getHeader(Constant.GATEWAY_TOKEN_HEADER);
        String gatewayToken = new String(Base64Utils.encode(Constant.GATEWAY_TOKEN_VALUE.getBytes()));
        if (StringUtils.equals(gatewayToken, token)) {
            return true;
        } else {
            response.setContentType("application/json;charset=UTF-8");
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            String msg ="请求访问："+request.getRequestURI()+"，认证失败，请通过网关访问资源";
            ServletUtils.renderString(response, JSON.toJSONString(JsonResults.error(403, msg)));
            return false;
        }
    }*/

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser==null){
            return false;
        }

        Long userId = SecurityUtils.getLoginUser().getUserId();
        System.out.println("userId =================== " + userId);
        return true;
    }
}

