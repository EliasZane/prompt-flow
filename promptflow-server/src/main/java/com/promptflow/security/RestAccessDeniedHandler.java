package com.promptflow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptflow.common.result.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) 
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        // 统一返回 403 状态码对应的 JSON
        Result<Void> result = Result.failed(403, "权限不足，无法访问该资源");

        response.getWriter().println(new ObjectMapper().writeValueAsString(result));
        response.getWriter().flush();
    }
}
