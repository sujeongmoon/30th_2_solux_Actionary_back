package com.req2res.actionarybe.domain.todo.repository;

import com.req2res.actionarybe.domain.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // 특정 유저 + 날짜 + 카테고리로 투두 조회
    List<Todo> findAllByUserIdAndDateAndCategoryId(Long userId, LocalDate date, Long categoryId);

    // 특정 유저 + 특정 날짜의 투두 조회
    List<Todo> findAllByUserIdAndDate(Long userId, LocalDate date);
}
