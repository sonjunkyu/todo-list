package com.example.todo.service;

import com.example.todo.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class MemberServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    PasswordEncoder passwordEncoder;

    private String loginId;
    private String name;
    private String password;

    @BeforeEach
    void setUp() {
        loginId = "testuser";
        name = "테스트유저";
        password = "password123";
    }

    @Test
    @DisplayName("회원가입 성공: 비밀번호가 암호화되어 저장된다")
    void register_hashes_password() {
        Member member = memberService.register(loginId, name, password);

        assertThat(member.getPassword()).isNotEqualTo(password);
        assertThat(passwordEncoder.matches(password, member.getPassword())).isTrue();
        assertThat(member.getLoginId()).isEqualTo(loginId);
    }

    @Test
    @DisplayName("회원가입 실패: 이미 존재하는 아이디로 가입할 수 없다")
    void register_fail_duplicate_id() {
        memberService.register(loginId, name, password);

        assertThatThrownBy(() -> memberService.register(loginId, "다른이름", "다른비밀번호"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 ID 입니다.");
    }

    @Test
    @DisplayName("로그인 성공: 아이디로 회원을 조회하고, 비밀번호가 일치한다")
    void login_success() {
        memberService.register(loginId, name, password);

        Member foundMember = memberService.findByLoginId(loginId);

        assertThat(foundMember).isNotNull();
        assertThat(passwordEncoder.matches(password, foundMember.getPassword())).isTrue();
    }

    @Test
    @DisplayName("로그인 실패: 존재하지 않는 아이디")
    void login_fail_not_found() {
        assertThatThrownBy(() -> memberService.findByLoginId("nonexistentuser"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("회원을 찾을 수 없습니다.");
    }
}