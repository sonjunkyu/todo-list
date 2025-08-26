package com.example.todo.security;

import com.example.todo.domain.Member;
import com.example.todo.dto.MemberDto;
import com.example.todo.service.MemberService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private final MemberDetailsService memberDetailsService;
    private MemberService memberService;

    public SecurityConfig(MemberDetailsService memberDetailsService) {
        this.memberDetailsService = memberDetailsService;
    }

    @Autowired
    public void setMemberService(@Lazy MemberService memberService) {
        this.memberService = memberService;
    }

    public static final String SESSION_KEY = "LOGIN_MEMBER";

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                
                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/signup", "/login", "/", "/styles.css", "/app.js", "/images/**", "/api/auth/**").permitAll()
                        .requestMatchers("/todos", "/todos.html", "/api/todos/**").authenticated()
                        .anyRequest().authenticated()
                )
                .userDetailsService(memberDetailsService)
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("id")
                        .passwordParameter("password")
                        .successHandler((req, res, auth) -> {
                            String loginId = auth.getName();
                            Member m = memberService.findByLoginId(loginId);
                            MemberDto dto = memberService.toDto(m);
                            HttpSession session = req.getSession(true);
                            session.setAttribute(SESSION_KEY, dto);
                            res.sendRedirect("/todos");
                        })
                        .failureUrl("/login?error")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                );
        return http.build();
    }
}
