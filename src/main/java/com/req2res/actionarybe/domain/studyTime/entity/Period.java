package com.req2res.actionarybe.domain.studyTime.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Period {
	DAY,
	WEEK,
	MONTH,
	YEAR;
}
