package com.example.todo.service;

import com.example.todo.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;

public interface LoginService {
    void login(LoginDto dto, HttpServletRequest request);
}
