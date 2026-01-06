package com.req2res.actionarybe.domain.search.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "스터디 검색 결과 항목")
public class StudySearchResponseDTO {

    @Schema(description = "스터디 ID", example = "12")
    private Long studyId;

    @Schema(description = "스터디 제목", example = "Spring Boot로 백엔드 스터디")
    private String title;

    @Schema(description = "스터디 설명(요약)", example = "기본기부터 게시판 만들기까지")
    private String description;

    @Schema(description = "스터디 카테고리", example = "LANGUAGE")
    private String category;

    @Schema(description = "썸네일 이미지 URL(없으면 null)", example = "https://.../thumb.png", nullable = true)
    private String thumbnailUrl;

    @JsonIgnore
    private boolean isJoined;

    @JsonGetter("isJoined")
    @Schema(description = "로그인한 사용자가 참여 중인지 여부(비로그인 시 false)", example = "false")
    public boolean getIsJoined() {
        return isJoined;
    }

    @Schema(description = "생성일", example = "2025-11-02T12:30:00")
    private LocalDateTime createdAt;
}
