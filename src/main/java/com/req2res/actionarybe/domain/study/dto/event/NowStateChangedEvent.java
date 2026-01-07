package com.req2res.actionarybe.domain.study.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NowStateChangedEvent {
	private Long studyId;
	private Long studyParticipantId;
	private String nowState;
}
