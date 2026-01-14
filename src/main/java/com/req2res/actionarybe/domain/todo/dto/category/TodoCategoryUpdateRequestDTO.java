package com.req2res.actionarybe.domain.todo.dto.category;
// 투두 카테고리 수정 API에서 사용하는 요청 DTO

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "투두 카테고리 수정 요청 DTO")
public class TodoCategoryUpdateRequestDTO {

    // 수정할 카테고리 이름 (선택)
    @Schema(
            description = "카테고리 이름 (선택)",
            example = "운동"
    )
    private String name;

    // 수정할 카테고리 색상 (선택)
    @Schema(
            description = "카테고리 색상 (HEX 코드, 선택),색상은 \\\\\\\"#D29AFA\\\\\\\", \\\\\\\"#6BEBFF\\\\\\\", \" +\n" +
                    "                    \"\\\\\\\"#9AFF5B\\\\\\\", \\\\\\\"#FFAD36\\\\\\\",\\\\\\\"#FF8355\\\\\\\", \\\\\\\"#FCDF2F\\\\\\\", \\\\\\\"#FF3D2F\\\\\\\", \\\\\\\"#FF9E97\\\\\\\"중에만 가능합니다.\"",
            example = "#6BEBFF"
    )
    private String color;
}
