package com.req2res.actionarybe.global.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {

	// 정상응답
	PARTICIPANT_JOINED,
	PARTICIPANT_LEFT,
	NOW_STATE_CHANGED,
	CHAT_MESSAGE,

	// 에러
	NOT_STUDY_PARTICIPANT;

}
