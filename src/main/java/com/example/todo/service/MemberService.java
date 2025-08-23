package com.example.todo.service;

import com.example.todo.domain.Member;
import com.example.todo.dto.MemberDto;

public interface MemberService {
    Member register(String loginId, String name, String password);
    Member findByLoginId(String loginId);
    MemberDto toDto(Member member);
}
