package com.req2res.actionarybe.domain.study.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.req2res.actionarybe.domain.image.service.ImageService;
import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.study.dto.JanusSessionRequestDto;
import com.req2res.actionarybe.domain.study.dto.JanusSessionResponseDto;
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
import com.req2res.actionarybe.domain.studyTime.entity.Type;
import com.req2res.actionarybe.domain.studyTime.repository.StudyTimeManualRepository;
import com.req2res.actionarybe.domain.studyTime.repository.StudyTimeRepository;
import com.req2res.actionarybe.global.config.JanusClient;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyService {

	private final JanusClient janusClient;
	private final StudyRepository studyRepository;
	private final StudyParticipantRepository studyParticipantRepository;
	private final StudyLikeRepository studyLikeRepository;
	private final StudyTimeRepository studyTimeRepository;
	private final StudyTimeManualRepository studyTimeManualRepository;

	private final ImageService imageService;

	@Value("${app.default.cover-image}")
	private String defaultCoverUrl;

	public StudyResponseDto createStudy(Member member, @Valid StudyRequestDto request, MultipartFile coverImage) {

		String encodedPassword = null;

		if (request.getPassword() != null && !request.getPassword().equals("")) {
			PasswordEncoder encoder = new BCryptPasswordEncoder();
			encodedPassword = encoder.encode(request.getPassword());
		}

		String coverImageUrl;
		if (coverImage != null && !coverImage.isEmpty()) {
			coverImageUrl = imageService.saveImage(coverImage);
		} else {
			coverImageUrl = defaultCoverUrl;
		}

		Study study = Study.builder()
			.name(request.getStudyName())
			.coverImage(coverImageUrl)
			.category(request.getCategory())
			.description(request.getDescription())
			.longDescription(request.getLongDescription())
			.memberLimit(request.getMemberLimit())
			.isPublic(request.getIsPublic())
			.password(encodedPassword)
			.creator(member)
			.build();

		studyRepository.save(study);

		try {
			janusClient.createStudyRoom(study.getId());
		} catch (Exception e) {
			studyRepository.delete(study);
			throw new CustomException(ErrorCode.STUDY_CREATE_ERROR);
		}

		return StudyResponseDto.from(study);
	}

	public JanusSessionResponseDto createJanusRoomId(JanusSessionRequestDto request) {

		Study study = studyRepository.findById(request.getStudyId()).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		try {
			janusClient.createStudyRoom(study.getId());
		} catch (Exception e) {
			throw new CustomException(ErrorCode.STUDY_CREATE_ERROR);
		}

		return JanusSessionResponseDto.builder()
			.studyId(request.getStudyId())
			.build();
	}

	public void deleteStudy(Member member, Long studyId) {
		Study study = studyRepository.findById(studyId).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		if (!study.getCreator().equals(member)) {
			throw new CustomException(ErrorCode.STUDY_NOT_MATCH_MEMBER);
		}

		if (studyParticipantRepository.countByStudyAndIsActiveTrue(study) != 0) {
			throw new CustomException(ErrorCode.STUDY_HAVE_USER);
		}

		imageService.deleteImage(study.getCoverImage());

		studyRepository.delete(study);
	}

	@Transactional
	public StudyResponseDto updateStudy(Member member, @Valid StudyRequestDto request, Long studyId,
		MultipartFile coverImage) {
		Study study = studyRepository.findById(studyId).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		if (!study.getCreator().equals(member)) {
			throw new CustomException(ErrorCode.STUDY_NOT_MATCH_MEMBER);
		}

		String coverImageUrl;
		if (coverImage != null && !coverImage.isEmpty()) {
			coverImageUrl = imageService.saveImage(coverImage);
		} else {
			coverImageUrl = member.getProfileImageUrl();
		}

		study.updateStudy(request, member, coverImageUrl);
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
			.memberNow(studyParticipantRepository.countByStudyAndIsActiveTrue(study))
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

	public List<Long> findUsersStudiedToday() {
		LocalDate today = LocalDate.now();
		LocalDateTime start = today.atStartOfDay();
		LocalDateTime end = today.atTime(LocalTime.MAX);

		List<Long> autoUserIds = studyTimeRepository.findDistinctUserIdsStudiedToday(
			start, end, Type.STUDY
		);

		List<Long> manualUserIds = studyTimeManualRepository.findDistinctUserIdsByManualDate(today);

		return Stream.concat(autoUserIds.stream(), manualUserIds.stream())
			.distinct()
			.toList();
	}

	public int getTodayTotalStudySeconds(Long userId) {
		LocalDate today = LocalDate.now();
		LocalDateTime start = today.atStartOfDay();
		LocalDateTime end = today.atTime(LocalTime.MAX);

		int autoSeconds = studyTimeRepository.sumStudySecondsTodayByUserId(
			userId, start, end, Type.STUDY
		);

		int manualSeconds = studyTimeManualRepository.sumManualStudySecondsByUserIdAndDate(userId, today);

		return autoSeconds + manualSeconds;
	}

	public String buildTodaySummaryText(Long userId) {
		int totalSeconds = getTodayTotalStudySeconds(userId);

		if (totalSeconds <= 0) {
			return "오늘 총 0분 공부했어요 👏";
		}

		int hours = totalSeconds / 3600;
		int minutes = (totalSeconds % 3600) / 60;

		if (hours == 0) {
			return "오늘 총 " + minutes + "분 공부했어요 👏";
		}
		return "오늘 총 " + hours + "시간 " + minutes + "분 공부했어요 👏";
	}
}
