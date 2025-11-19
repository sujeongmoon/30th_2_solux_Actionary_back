package com.req2res.actionarybe.domain.todo.repository;

import com.req2res.actionarybe.domain.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // 날짜만으로 조회 (카테고리 필터 없음)
    List<Todo> findAllByDate(LocalDate date);

    // 날짜 + 카테고리로 조회
    List<Todo> findAllByDateAndCategoryId(LocalDate date, Long categoryId);
}
