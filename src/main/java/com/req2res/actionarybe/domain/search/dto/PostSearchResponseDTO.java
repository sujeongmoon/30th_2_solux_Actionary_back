package com.req2res.actionarybe.domain.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "게시글 검색 결과 요약 정보 DTO")
public class PostSearchResponseDTO {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(
            description = "게시글 말머리 타입",
            example = "인증",
            allowableValues = {"인증", "소통", "질문", "구인", "정보"}
    )
    private String type;

    @Schema(description = "게시글 제목", example = "알고리즘 문제 풀기 10일차 인증")
    private String title;

    @Schema(description = "작성자 닉네임", example = "dear_dahyun")
    private String authorNickname;

    @Schema(description = "게시글 작성일시", example = "2025-11-02T11:10:00")
    private LocalDateTime createdAt;

    @Schema(description = "댓글 개수", example = "3")
    private int commentCount;

    @Schema(
            description = "내가 작성한 게시글 여부 (비로그인 시 false)",
            example = "true"
    )
    @JsonProperty("isMine")
    private boolean isMine;
}
