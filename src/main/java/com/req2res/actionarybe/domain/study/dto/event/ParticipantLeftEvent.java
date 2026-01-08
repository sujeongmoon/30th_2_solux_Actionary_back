package com.req2res.actionarybe.domain.study.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantLeftEvent {
	private Long studyId;
	private Long studyParticipantId;
}
