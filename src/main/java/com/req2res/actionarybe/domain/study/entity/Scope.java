package com.req2res.actionarybe.domain.study.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Scope {

	ALL("전체"),
	OWNED("개설한 스터디"),
	JOINED("참가한 스터디"),
	LIKED("즐겨찾기");

	private final String label;
}
