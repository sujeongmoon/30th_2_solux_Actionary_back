package com.req2res.actionarybe.domain.todo.repository;

import com.req2res.actionarybe.domain.todo.entity.TodoCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoCategoryRepository extends JpaRepository<TodoCategory, Long> {

    //카테고리 존재 + 소유자 검증하는 메소드
    Optional<TodoCategory> findByIdAndUserId(Long id, Long userId);

    //같은 사용자, 카테고리 이름이 있는지 검사하는 메소드
    boolean existsByUserIdAndName(Long userId, String name);

    //유저 소유 + 존재 체크 메서드
    boolean existsByIdAndUserId(Long id, Long userId);

    //특정 사용자로 최신순으로 카테고리 찾는 메소드
    List<TodoCategory> findAllByUserIdOrderByCreatedAtAsc(Long userId);


}

