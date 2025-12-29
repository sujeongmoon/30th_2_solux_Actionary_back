package com.req2res.actionarybe.domain.todo.dto.category;
//투두 카테고리 생성 API에서 사용하는 DTO

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TodoCategoryCreateRequestDTO {

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "카테고리 색상은 필수입니다.")
    private String color;
}
