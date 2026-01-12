package com.req2res.actionarybe.domain.aisummary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "AI URL 요약 요청 DTO")
public class AiSummaryUrlRequestDTO {

    @NotBlank(message = "sourceUrl은 필수입니다.")
    @Schema(
            description = "요약할 원본 문서 URL",
            example = "https://example.com/sample.pdf",
            required = true
    )
    private String sourceUrl;

    @Schema(
            description = "요약 언어 (기본값: ko)",
            example = "ko,en",
            defaultValue = "ko"
    )
    private String language = "ko";

    @Schema(
            description = "최대 토큰 수 (요약 길이 제한)",
            example = "1000",
            defaultValue = "600"
    )
    private Integer maxTokens = 600;
}
