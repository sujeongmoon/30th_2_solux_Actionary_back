package com.req2res.actionarybe.domain.todo.dto.category;
// 투두 카테고리 생성 API에서 사용하는 요청 DTO

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "투두 카테고리 생성 요청 DTO")
public class TodoCategoryCreateRequestDTO {

    // 카테고리 이름
    // 사용자별로 중복 불가
    @Schema(
            description = "카테고리 이름",
            example = "공부"
    )
    @NotBlank(message = "카테고리 이름은 필수입니다.")
    private String name;

    // 카테고리 색상
    @Schema(
            description = "카테고리 색상 (HEX 코드),색상은 \\\\\\\"#D29AFA\\\\\\\", \\\\\\\"#6BEBFF\\\\\\\", \" +\n" +
                    "                    \"\\\\\\\"#9AFF5B\\\\\\\", \\\\\\\"#FFAD36\\\\\\\",\\\\\\\"#FF8355\\\\\\\", \\\\\\\"#FCDF2F\\\\\\\", \\\\\\\"#FF3D2F\\\\\\\", \\\\\\\"#FF9E97\\\\\\\"중에만 가능합니다.\"",
            example = "#D29AFA"
    )
    @NotBlank(message = "카테고리 색상은 필수입니다.")
    private String color;
}
