package com.req2res.actionarybe.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UpdatePostRequestDTO {

    @Schema(example = "인증")
    private String type;

    @Schema(example = "수정된 스터디")
    private String title;

    @Schema(example = "ERD 설계는 정말 중요합니다.")
    private String text;
}
