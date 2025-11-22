package com.req2res.actionarybe.domain.studyTime.service;

import org.springframework.stereotype.Service;

import com.req2res.actionarybe.domain.studyTime.repository.StudyTimeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyTimeService {

	private final StudyTimeRepository studyTimeRepository;

}
