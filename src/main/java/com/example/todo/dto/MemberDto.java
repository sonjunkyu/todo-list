package com.example.todo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private Long id;

    @NotBlank
    private String loginId;

    @NotBlank
    private String name;
}
