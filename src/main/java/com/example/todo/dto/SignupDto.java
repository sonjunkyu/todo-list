package com.example.todo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupDto {
    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String password;
}