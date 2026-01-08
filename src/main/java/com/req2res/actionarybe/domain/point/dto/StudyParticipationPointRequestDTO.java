package com.req2res.actionarybe.domain.point.dto;
// 스터디 참여 포인트 적립 API에서 사용하는 request DTO

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "스터디 참여 포인트 적립 요청 DTO")
public class StudyParticipationPointRequestDTO {

    @Schema(
            description = "참여한 스터디 방 ID",
            example = "12",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "studyRoomId는 필수입니다.")
    private Long studyRoomId;

    @Schema(
            description = "스터디 참여 시간 (분 단위, 30분 이상일 때만 포인트 적립)",
            example = "35",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("participatedMinutes")
    @NotNull(message = "participatedMinutes는 필수입니다.")
    private Integer participatedMinutes;
}
