package com.req2res.actionarybe.domain.study.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantPrivateRequestDto;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantUserDto;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantUsersResponseDto;
import com.req2res.actionarybe.domain.study.entity.Study;
import com.req2res.actionarybe.domain.study.entity.StudyParticipant;
import com.req2res.actionarybe.domain.study.repository.StudyParticipantRepository;
import com.req2res.actionarybe.domain.study.repository.StudyRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyParticipantService {

	private final StudyRepository studyRepository;
	private final StudyParticipantRepository studyParticipantRepository;

	public StudyParticipantResponseDto createStudyParticipantPublic(Member member, Long studyId) {

		Study study = studyRepository.findById(studyId).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		if (studyParticipantRepository.countByStudyAndIsActiveTrue(study) >= study.getMemberLimit()) {
			throw new CustomException(ErrorCode.STUDY_CAPACITY_EXCEEDED);
		}

		if (!study.getIsPublic()) {
			throw new CustomException(ErrorCode.STUDY_PARTICIPANT_PASSWORD_REQUIRED);
		}

		StudyParticipant studyParticipant = StudyParticipant.builder()
			.study(study)
			.member(member)
			.isActive(true)
			.build();

		studyParticipantRepository.save(studyParticipant);

		return StudyParticipantResponseDto.from(studyParticipant);
	}

	public StudyParticipantResponseDto createStudyParticipantPrivate(Member member, Long studyId,
		@Valid StudyParticipantPrivateRequestDto request) {

		Study study = studyRepository.findById(studyId).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		if (studyParticipantRepository.countByStudyAndIsActiveTrue(study) >= study.getMemberLimit()) {
			throw new CustomException(ErrorCode.STUDY_CAPACITY_EXCEEDED);
		}

		if (study.getIsPublic()) {
			throw new CustomException(ErrorCode.STUDY_PARTICIPANT_PASSWORD_UNREQUIRED);
		}

		PasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!encoder.matches(request.getPassword(), study.getPassword())) {
			throw new CustomException(
				ErrorCode.STUDY_PARTICIPANT_PASSWORD_BAD_CREDENTIALS
			);
		}

		StudyParticipant studyParticipant = StudyParticipant.builder()
			.study(study)
			.member(member)
			.isActive(true)
			.build();

		studyParticipantRepository.save(studyParticipant);

		return StudyParticipantResponseDto.from(studyParticipant);
	}

	public StudyParticipantUsersResponseDto getStudyParticipantUsers(Member member, Long studyId) {

		Study study = studyRepository.findById(studyId).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		StudyParticipantUserDto me = studyParticipantRepository.findParticipantUserByStudyAndMemberAndIsActiveTrue(
				studyId, member.getId())
			.orElseThrow(() ->
				new CustomException(ErrorCode.STUDY_PARTICIPANT_NOT_JOINED)
			);

		return StudyParticipantUsersResponseDto.builder()
			.studyId(studyId)
			.me(me)
			.participatingUsers(studyParticipantRepository.findParticipantUserByStudyAndIsActiveTrue(studyId))
			.build();
	}
}
