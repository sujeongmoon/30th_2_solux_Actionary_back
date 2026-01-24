package com.req2res.actionarybe.domain.studyTime.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Type {
	STUDY("공부 시간"),
	BREAK("휴식 시간");

	private final String label;

}
