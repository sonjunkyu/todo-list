package com.example.todo.controller;

import com.example.todo.domain.Member;
import com.example.todo.dto.MemberDto;
import com.example.todo.dto.TodoDto;
import com.example.todo.service.MemberService;
import com.example.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoService todoService;
    private final MemberService memberService;

    @GetMapping
    public List<TodoDto> list(@SessionAttribute("LOGIN_MEMBER") MemberDto me) {
        return todoService.list(currentMember(me));
    }

    @PostMapping
    public TodoDto create(@SessionAttribute("LOGIN_MEMBER") MemberDto me, @RequestBody TodoDto req) {
        return todoService.create(currentMember(me), req);
    }

    @PatchMapping("/{id}")
    public TodoDto update(@SessionAttribute("LOGIN_MEMBER") MemberDto me, @PathVariable Long id, @RequestBody TodoDto req) {
        return todoService.update(currentMember(me), id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@SessionAttribute("LOGIN_MEMBER") MemberDto me, @PathVariable Long id) {
        todoService.delete(currentMember(me), id);
        return ResponseEntity.noContent().build();
    }

    private Member currentMember(MemberDto session) {
        return memberService.findByLoginId(session.getLoginId());
    }
}
