package com.example.todo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDto {
    @NotBlank
    private String id; // loginId

    @NotBlank
    private String password;
}
