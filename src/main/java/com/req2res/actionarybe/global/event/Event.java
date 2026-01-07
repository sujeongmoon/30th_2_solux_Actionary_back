package com.req2res.actionarybe.global.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Event<T> {

	private EventType type;
	private T data;

}
