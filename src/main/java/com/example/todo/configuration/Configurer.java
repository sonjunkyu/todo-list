package com.example.todo.configuration;

import com.example.todo.configuration.interceptor.LoginInterceptor;
import com.example.todo.configuration.interceptor.ToDoInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class Configurer implements WebMvcConfigurer {
    private final LoginInterceptor loginInterceptor;
    private final ToDoInterceptor toDoInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/todos.html", "/api/todos/**");
        registry.addInterceptor(toDoInterceptor)
                .addPathPatterns("/api/todos/**");
    }
}
