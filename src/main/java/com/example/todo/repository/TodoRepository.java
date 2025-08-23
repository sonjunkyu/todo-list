package com.example.todo.repository;

import com.example.todo.domain.Member;
import com.example.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByMemberOrderByCreatedAtDesc(Member member);
    Optional<Todo> findByIdAndMember(Long id, Member member);
}
