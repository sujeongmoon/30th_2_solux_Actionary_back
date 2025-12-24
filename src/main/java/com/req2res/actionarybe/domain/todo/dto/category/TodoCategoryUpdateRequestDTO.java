package com.req2res.actionarybe.domain.todo.dto.category;
//투두 카테고리 수정 API에서 사용할 Reqeust DTO

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TodoCategoryUpdateRequestDTO {

    private String name;  // 선택
    private String color; // 선택
}

