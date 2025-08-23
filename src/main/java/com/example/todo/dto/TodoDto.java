package com.example.todo.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDto {
    private Long id;

    @NotBlank
    private String title;

    private String description;

    private LocalDateTime createdAt;

    @NotNull
    @FutureOrPresent
    private LocalDate dueDate;

    private boolean completed;
}
