package com.example.todo.service;

import com.example.todo.domain.Member;
import com.example.todo.dto.MemberDto;
import com.example.todo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member register(String loginId, String name, String password) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("이미 존재하는 ID 입니다.");
        }

        Member member = Member.builder()
                .loginId(loginId)
                .name(name)
                .password(passwordEncoder.encode(password))
                .build();

        return memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Member findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }

    @Override
    public MemberDto toDto(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .build();
    }
}
