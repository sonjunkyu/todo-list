package com.example.todo.service;

import com.example.todo.domain.Member;
import com.example.todo.dto.TodoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class TodoServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    TodoService todoService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberService.register("testuser", "테스트유저", "password");
    }

    @Test
    @DisplayName("Todo 생성 성공")
    void create_todo_success() {
        TodoDto req = TodoDto.builder()
                .title("새로운 Todo")
                .description("설명입니다")
                .dueDate(LocalDate.now().plusDays(1))
                .build();

        TodoDto createdTodo = todoService.create(member, req);

        assertThat(createdTodo.getId()).isNotNull();
        assertThat(createdTodo.getTitle()).isEqualTo("새로운 Todo");
        assertThat(createdTodo.isCompleted()).isFalse();

        List<TodoDto> todos = todoService.list(member);
        assertThat(todos).hasSize(1);
        assertThat(todos.get(0).getTitle()).isEqualTo("새로운 Todo");
    }

    @Test
    @DisplayName("Todo 생성 실패: 마감일은 생성일 이전일 수 없다")
    void dueDate_must_not_be_before_created() {
        TodoDto req = TodoDto.builder()
                .title("잘못된 Todo")
                .dueDate(LocalDate.now().minusDays(1))
                .build();

        assertThatThrownBy(() -> todoService.create(member, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("마감일은 생성일 이전으로 설정할 수 없습니다.");
    }

    @Test
    @DisplayName("Todo 수정 성공")
    void update_todo_success() {
        TodoDto originalTodo = todoService.create(member, TodoDto.builder().title("원본").dueDate(LocalDate.now()).build());

        TodoDto updateReq = TodoDto.builder()
                .title("수정된 제목")
                .description("수정된 설명")
                .build();
        TodoDto updatedTodo = todoService.update(member, originalTodo.getId(), updateReq);

        assertThat(updatedTodo.getTitle()).isEqualTo("수정된 제목");
        assertThat(updatedTodo.getDescription()).isEqualTo("수정된 설명");
    }

    @Test
    @DisplayName("Todo 완료 상태 토글 성공")
    void toggle_completion_success() {
        TodoDto originalTodo = todoService.create(member, TodoDto.builder().title("원본").dueDate(LocalDate.now()).build());
        assertThat(originalTodo.isCompleted()).isFalse();

        TodoDto toggleReq = TodoDto.builder().completed(true).build();
        TodoDto toggledTodo = todoService.update(member, originalTodo.getId(), toggleReq);

        assertThat(toggledTodo.isCompleted()).isTrue();

        TodoDto toggleBackReq = TodoDto.builder().completed(false).build();
        TodoDto toggledBackTodo = todoService.update(member, toggledTodo.getId(), toggleBackReq);

        assertThat(toggledBackTodo.isCompleted()).isFalse();
    }


    @Test
    @DisplayName("Todo 삭제 성공")
    void delete_todo_success() {
        TodoDto todoToDelete = todoService.create(member, TodoDto.builder().title("삭제될 Todo").dueDate(LocalDate.now()).build());
        assertThat(todoService.list(member)).hasSize(1);

        todoService.delete(member, todoToDelete.getId());

        assertThat(todoService.list(member)).isEmpty();
    }

    @Test
    @DisplayName("Todo 수정/삭제 실패: 다른 사용자의 Todo는 수정/삭제할 수 없다")
    void update_delete_fail_not_authorized() {
        Member anotherMember = memberService.register("anotheruser", "다른유저", "password");
        TodoDto originalTodo = todoService.create(member, TodoDto.builder().title("내 Todo").dueDate(LocalDate.now()).build());

        TodoDto updateReq = TodoDto.builder().title("수정 시도").build();
        assertThatThrownBy(() -> todoService.update(anotherMember, originalTodo.getId(), updateReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 Todo가 없거나 권한이 없습니다.");

        assertThatThrownBy(() -> todoService.delete(anotherMember, originalTodo.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 Todo가 없거나 권한이 없습니다.");
    }
}