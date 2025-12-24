package com.req2res.actionarybe.domain.todo.dto.category;
//투두 카테고리 목록 조회 API에서 사용할 DTO

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoCategoryListItemDTO {
    private Long categoryId;
    private String name;
    private String color;
    private LocalDateTime createdAt;
}
