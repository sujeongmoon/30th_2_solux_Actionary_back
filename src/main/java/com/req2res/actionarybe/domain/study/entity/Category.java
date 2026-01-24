package com.req2res.actionarybe.domain.study.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
	CSAT("수능"),
	CIVIL_SERVICE("공무원"),
	TEACHER_EXAM("임용"),
	LICENSE("자격증"),
	LANGUAGE("어학"),
	EMPLOYMENT("취업"),
	OTHER("기타");

	private final String label;

}
