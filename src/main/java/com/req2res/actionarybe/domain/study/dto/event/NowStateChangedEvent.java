package com.req2res.actionarybe.domain.study.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NowStateChangedEvent {

	private Long studyParticipantId;
	private Long studyId;
	private Long userId;
	private String nowState;

}
