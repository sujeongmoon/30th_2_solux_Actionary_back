package com.req2res.actionarybe.domain.studyTime.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.req2res.actionarybe.domain.study.entity.StudyParticipant;
import com.req2res.actionarybe.domain.studyTime.entity.StudyTime;
import com.req2res.actionarybe.domain.studyTime.entity.Type;
import com.req2res.actionarybe.domain.studyTime.repository.StudyTimeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyTimeService {

	private final StudyTimeRepository studyTimeRepository;

	public StudyTime createStudyTime(Type type, StudyParticipant studyParticipant) {

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime changedAt = studyParticipant.getLastStateChangedAt();
		int durationSeconds = (int)Duration.between(changedAt, now).getSeconds();

		StudyTime studyTime = StudyTime.builder()
			.studyParticipant(studyParticipant)
			.type(type)
			.durationSecond(durationSeconds)
			.build();

		studyTimeRepository.save(studyTime);

		return studyTime;
	}

}
