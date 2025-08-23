package com.example.todo.service;

import com.example.todo.domain.Member;
import com.example.todo.domain.Todo;
import com.example.todo.dto.TodoDto;
import com.example.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoServiceImpl implements TodoService {
    private final TodoRepository todoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TodoDto> list(Member owner) {
        return todoRepository.findByMemberOrderByCreatedAtDesc(owner)
                .stream().map(this::toDto).toList();
    }

    @Override
    public TodoDto create(Member owner, TodoDto req) {
        Todo todo = new Todo();
        todo.setMember(owner);
        todo.setTitle(req.getTitle());
        todo.setDescription(req.getDescription());
        validateDueDateOnCreate(req.getDueDate());
        todo.setDueDate(req.getDueDate());
        todo.setCompleted(false);

        return toDto(todoRepository.save(todo));
    }

    @Override
    public TodoDto update(Member owner, Long todoId, TodoDto req) {
        Todo todo = todoRepository.findByIdAndMember(todoId, owner)
                .orElseThrow(() -> new IllegalArgumentException("해당 Todo가 없거나 권한이 없습니다."));
        if (req.getTitle() != null) todo.setTitle(req.getTitle());
        if (req.getDescription() != null) todo.setDescription(req.getDescription());
        if (req.getDueDate() != null) validateDueDateOnUpdate(todo, req.getDueDate());
        if (req.getDueDate() != null) todo.setDueDate(req.getDueDate());

        todo.setCompleted(req.isCompleted());
        return toDto(todo);
    }

    @Override
    public void delete(Member owner, Long todoId) {
        Todo todo = todoRepository.findByIdAndMember(todoId, owner)
                .orElseThrow(() -> new IllegalArgumentException("해당 Todo가 없거나 권한이 없습니다."));
        todoRepository.delete(todo);
    }

    private void validateDueDateOnCreate(LocalDate due) {
        LocalDate today = LocalDate.now();
        if (due.isBefore(today)) {
            throw new IllegalArgumentException("마감일은 생성일 이전으로 설정할 수 없습니다.");
        }
    }

    private void validateDueDateOnUpdate(Todo existing, LocalDate newDue) {
        LocalDate createdDate = existing.getCreatedAt().toLocalDate();
        if (newDue.isBefore(createdDate)) {
            throw new IllegalArgumentException("마감일은 생성일 이전으로 설정할 수 없습니다.");
        }
    }

    private TodoDto toDto(Todo todo) {
        return TodoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .createdAt(todo.getCreatedAt())
                .dueDate(todo.getDueDate())
                .completed(todo.isCompleted())
                .build();
    }
}
