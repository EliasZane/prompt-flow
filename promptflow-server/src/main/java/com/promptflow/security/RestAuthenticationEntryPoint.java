package com.promptflow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptflow.common.result.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) 
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        
        // 统一返回 401 状态码对应的 JSON
        Result<Void> result = Result.failed(401, "登录已过期或未登录，请重新登录");
        
        response.getWriter().println(new ObjectMapper().writeValueAsString(result));
        response.getWriter().flush();
    }
}
