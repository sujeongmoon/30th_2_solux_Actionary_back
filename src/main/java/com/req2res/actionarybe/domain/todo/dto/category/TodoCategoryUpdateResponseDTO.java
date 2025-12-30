package com.req2res.actionarybe.domain.todo.dto.category;
//투두 카테고리 수정 API에서 사용할 response DTO
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoCategoryUpdateResponseDTO {
    private Long categoryId;
    private String name;
    private String color;
}

