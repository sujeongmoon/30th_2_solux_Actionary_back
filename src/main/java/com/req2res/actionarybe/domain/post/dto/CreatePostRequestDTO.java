package com.req2res.actionarybe.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePostRequestDTO {

    @Schema(example = "1")
    @JsonProperty(value = "member_id")
    private Long membreId;

    @Schema(example = "인증")
    @NotBlank
    private String type;

    @Schema(example = "오늘의 스터디")
    @NotBlank
    private String title;

    @NotNull
    private PostContentRequestDTO content;
}

