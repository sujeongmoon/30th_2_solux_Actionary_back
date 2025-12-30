package com.req2res.actionarybe.domain.point.dto;
//스터디 참여 포인트 적립 API에서 사용하는 response DTO

import com.req2res.actionarybe.domain.point.entity.PointSource;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyParticipationPointResponseDTO {
    private Long userId;
    private Long studyRoomId;
    private int earnedPoint;
    private PointSource source;
    private int totalPoint;
}

