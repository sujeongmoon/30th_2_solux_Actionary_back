package com.req2res.actionarybe.domain.point.dto;
//스터디 참여 포인트 적립 API에서 사용하는 request DTO

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyParticipationPointRequestDTO {

    @NotNull(message = "studyRoomId는 필수입니다.")
    private Long studyRoomId;

    @JsonProperty("participatedMinutes")
    @NotNull(message = "participatedMinutes는 필수입니다.")
    private Integer participatedMinutes;
}

