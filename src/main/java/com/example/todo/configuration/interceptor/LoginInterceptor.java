package com.example.todo.configuration.interceptor;

import com.example.todo.security.SecurityConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object loginMember = request.getSession(false) == null ? null : request.getSession(false).getAttribute(SecurityConfig.SESSION_KEY);
        if (loginMember == null) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}
