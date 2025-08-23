package com.example.todo.controller;

import com.example.todo.domain.Member;
import com.example.todo.dto.MemberDto;
import com.example.todo.service.MemberService;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LoginController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberDto> signup(@RequestBody SignupReq req) {
        Member member = memberService.register(req.id, req.name, req.password);
        return ResponseEntity.ok(memberService.toDto(member));
    }

    // 현재 로그인 사용자 확인
    @GetMapping("/me")
    public ResponseEntity<MemberDto> me(
            @SessionAttribute(name = "LOGIN_MEMBER", required = false) MemberDto me) {
        if (me == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(me);
        }
    }

    // 회원가입 요청을 담은 DTO
    @Data
    static class SignupReq {
        @NotBlank
        public String id;

        @NotBlank
        public String name;

        @NotBlank
        public String password;
    }
}
