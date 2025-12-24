package com.req2res.actionarybe.domain.todo.dto.category;
//투두 카테고리 생성API에서 사용하는 response DTO
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoCategoryCreateResponseDTO {
    private Long categoryId;
    private String name;
    private String color;
    private LocalDateTime createdAt;
}
