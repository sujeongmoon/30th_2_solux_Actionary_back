package com.req2res.actionarybe.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Event<T> {
	private String type;
	private T data;
}
