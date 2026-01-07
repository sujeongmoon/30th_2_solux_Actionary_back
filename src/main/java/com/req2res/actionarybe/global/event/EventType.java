package com.req2res.actionarybe.global.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {

	PARTICIPANT_JOINED,
	PARTICIPANT_LEFT,
	NOW_STATE_CHANGED;

}
