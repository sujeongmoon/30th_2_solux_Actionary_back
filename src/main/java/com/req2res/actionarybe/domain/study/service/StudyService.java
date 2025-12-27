package com.req2res.actionarybe.domain.study.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.study.dto.StudyRequestDto;
import com.req2res.actionarybe.domain.study.dto.StudyResponseDto;
import com.req2res.actionarybe.domain.study.entity.Study;
import com.req2res.actionarybe.domain.study.repository.StudyRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyService {

	private final StudyRepository studyRepository;

	public StudyResponseDto createStudy(Member member, @Valid StudyRequestDto request) {

		String encodedPassword = null;

		if (request.getPassword() != null && !request.getPassword().equals("")) {
			PasswordEncoder encoder = new BCryptPasswordEncoder();
			encodedPassword = encoder.encode(request.getPassword());
		}

		// TODO: 이미지 값 안 받는 경우 기본이미지 불러올 수 있도록

		Study study = Study.builder()
			.name(request.getStudyName())
			.coverImage(request.getCoverImage())
			.category(request.getCategory())
			.description(request.getDescription())
			.memberLimit(request.getMemberLimit())
			.isPublic(request.getIsPublic())
			.password(encodedPassword)
			.creator(member)
			.build();

		studyRepository.save(study);

		return StudyResponseDto.from(study);
	}
}
