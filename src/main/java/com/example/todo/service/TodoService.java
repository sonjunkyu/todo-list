package com.example.todo.service;

import com.example.todo.domain.Member;
import com.example.todo.dto.TodoDto;

import java.util.List;

public interface TodoService {
    List<TodoDto> list(Member owner);
    TodoDto create(Member owner, TodoDto req);
    TodoDto update(Member owner, Long todoId, TodoDto req);
    void delete(Member owner, Long todoId);
}
