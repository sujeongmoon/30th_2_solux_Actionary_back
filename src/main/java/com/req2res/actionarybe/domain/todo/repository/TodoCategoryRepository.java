package com.req2res.actionarybe.domain.todo.repository;

import com.req2res.actionarybe.domain.todo.entity.TodoCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoCategoryRepository extends JpaRepository<TodoCategory, Long> {

    //같은 사용자, 카테고리 이름이 있는지 검사하는 메소드
    boolean existsByUserIdAndName(Long userId, String name);
}

