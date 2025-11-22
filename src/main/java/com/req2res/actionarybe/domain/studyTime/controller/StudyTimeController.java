package com.req2res.actionarybe.domain.studyTime.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.req2res.actionarybe.domain.studyTime.service.StudyTimeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studytimes")
public class StudyTimeController {

	private final StudyTimeService studyTimeService;

}
