package com.req2res.actionarybe.domain.study.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.study.dto.StudyDetailResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudyListResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudyRequestDto;
import com.req2res.actionarybe.domain.study.dto.StudyResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudySummaryDto;
import com.req2res.actionarybe.domain.study.entity.Category;
import com.req2res.actionarybe.domain.study.entity.Study;
import com.req2res.actionarybe.domain.study.repository.StudyLikeRepository;
import com.req2res.actionarybe.domain.study.repository.StudyParticipantRepository;
import com.req2res.actionarybe.domain.study.repository.StudyRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyService {

	private final StudyRepository studyRepository;
	private final StudyParticipantRepository studyParticipantRepository;
	private final StudyLikeRepository studyLikeRepository;

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

	public void deleteStudy(Member member, Long studyId) {
		Study study = studyRepository.findById(studyId).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		if (!study.getCreator().equals(member)) {
			throw new CustomException(ErrorCode.STUDY_NOT_MATCH_MEMBER);
		}

		if (studyParticipantRepository.countByStudyAndUpdatedAtIsNull(study) != 0) {
			throw new CustomException(ErrorCode.STUDY_NOT_MATCH_MEMBER);
		}

		studyRepository.delete(study);
	}

	public StudyResponseDto updateStudy(Member member, @Valid StudyRequestDto request, Long studyId) {
		Study study = studyRepository.findById(studyId).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		if (!study.getCreator().equals(member)) {
			throw new CustomException(ErrorCode.STUDY_NOT_MATCH_MEMBER);
		}

		study.updateStudy(request, member);
		return StudyResponseDto.from(study);
	}

	public StudyDetailResponseDto getStudyDetail(Member member, Long studyId) {
		Study study = studyRepository.findById(studyId).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		return StudyDetailResponseDto.builder()
			.studyId(studyId)
			.studyName(study.getName())
			.coverImage(study.getCoverImage())
			.category(study.getCategory())
			.categoryLabel(study.getCategory().getLabel())
			.description(study.getDescription())
			.memberNow(studyParticipantRepository.countByStudyAndUpdatedAtIsNull(study))
			.memberLimit(study.getMemberLimit())
			.isPublic(study.getIsPublic())
			.isStudyLike(studyLikeRepository.existsByStudyAndMember(study, member))
			.isStudyOwner(study.getCreator().equals(member))
			.build();
	}

	public StudyListResponseDto getStudyList(String visibility, Category category, int pageNumber) {

		Pageable pageable = PageRequest.of(pageNumber, 8, Sort.by("updatedAt").descending());

		Boolean isPublic = null;

		if (visibility.equals("public")) {
			isPublic = true;
		} else if (visibility.equals("private")) {
			isPublic = false;
		}

		Page<Study> studyPage;

		if (category != null) {
			studyPage = studyRepository.findByIsPublicAndCategory(isPublic, category, pageable);
		} else {
			studyPage = studyRepository.findByIsPublic(isPublic, pageable);
		}

		return StudyListResponseDto.builder()
			.isPublic(isPublic)
			.category(category == null ? null : category)
			.categoryLabel(category == null ? null : category.getLabel())
			.content(studyPage.getContent().stream()
				.map(StudySummaryDto::from)
				.toList())
			.page(studyPage.getNumber())
			.size(studyPage.getSize())
			.totalElements(studyPage.getTotalElements())
			.totalPages(studyPage.getTotalPages())
			.build();
	}
}
