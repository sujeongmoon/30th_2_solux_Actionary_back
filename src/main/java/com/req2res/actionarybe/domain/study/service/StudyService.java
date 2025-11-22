package com.req2res.actionarybe.domain.study.service;

import org.springframework.stereotype.Service;

import com.req2res.actionarybe.domain.study.repository.StudyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyService {

	private final StudyRepository studyRepository;
}
