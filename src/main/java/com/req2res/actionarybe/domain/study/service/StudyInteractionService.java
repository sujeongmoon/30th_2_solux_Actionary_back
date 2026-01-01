package com.req2res.actionarybe.domain.study.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.study.dto.HitStudyListResponseDto;
import com.req2res.actionarybe.domain.study.dto.RankingBoardDto;
import com.req2res.actionarybe.domain.study.dto.RankingDurationDto;
import com.req2res.actionarybe.domain.study.dto.StudyInteractionSummaryDto;
import com.req2res.actionarybe.domain.study.dto.StudyLikeResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudyRankingBoardListResponseDto;
import com.req2res.actionarybe.domain.study.entity.Study;
import com.req2res.actionarybe.domain.study.entity.StudyLike;
import com.req2res.actionarybe.domain.study.repository.StudyLikeRepository;
import com.req2res.actionarybe.domain.study.repository.StudyParticipantRepository;
import com.req2res.actionarybe.domain.study.repository.StudyRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyInteractionService {

	private final StudyRepository studyRepository;
	private final StudyParticipantRepository studyParticipantRepository;
	private final StudyLikeRepository studyLikeRepository;

	public StudyRankingBoardListResponseDto getRankingBoardStudyList(Long studyId, String type) {
		boolean isToday = true;

		if (type.equals("today")) {
			isToday = true;
		} else if (type.equals("total")) {
			isToday = false;
		}

		LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

		List<RankingDurationDto> todayList =
			studyParticipantRepository.findTodayDurations(studyId, startOfDay);

		List<RankingDurationDto> totalList =
			studyParticipantRepository.findTotalDurations(studyId);

		Map<Long, RankingDurationDto> todayMap =
			todayList.stream()
				.collect(Collectors.toMap(
					RankingDurationDto::getUserId,
					Function.identity()
				));

		Map<Long, RankingDurationDto> totalMap =
			totalList.stream()
				.collect(Collectors.toMap(
					RankingDurationDto::getUserId,
					Function.identity()
				));

		List<RankingDurationDto> baseList =
			isToday ? todayList : totalList;

		List<RankingBoardDto> rankingBoards = baseList.stream()
			.sorted(Comparator.comparingLong(RankingDurationDto::getDurationSeconds).reversed())
			.map(base -> {
				Long today = todayMap.getOrDefault(
					base.getUserId(),
					new RankingDurationDto(base.getUserId(), base.getUserNickname(), 0L)
				).getDurationSeconds();

				Long total = totalMap.getOrDefault(
					base.getUserId(),
					new RankingDurationDto(base.getUserId(), base.getUserNickname(), 0L)
				).getDurationSeconds();

				return RankingBoardDto.builder()
					.userId(base.getUserId())
					.userNickname(base.getUserNickname())
					.todayDurationSeconds(today)
					.totalDurationSeconds(total)
					.build();
			})
			.toList();

		return StudyRankingBoardListResponseDto.builder()
			.studyId(studyId)
			.isToday(isToday)
			.rankingBoards(rankingBoards)
			.build();
	}

	public HitStudyListResponseDto getHitStudyList(int pageNumber) {

		Pageable pageable = PageRequest.of(pageNumber, 3);

		Page<StudyInteractionSummaryDto> page = studyRepository.findHitStudies(pageable);

		return HitStudyListResponseDto.builder()
			.content(page.getContent())
			.page(page.getNumber())
			.size(page.getSize())
			.totalElements(page.getTotalElements())
			.totalPages(page.getTotalPages())
			.build();
	}

	public StudyLikeResponseDto createStudyLike(Member member, Long studyId) {

		Study study = studyRepository.findById(studyId).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		boolean isLiked = !studyLikeRepository.existsByStudyAndMember(study, member);

		if (isLiked) {
			StudyLike studyLike = StudyLike.builder()
				.study(study)
				.member(member)
				.build();
			studyLikeRepository.save(studyLike);
		} else {
			StudyLike studyLike = studyLikeRepository.findByStudyAndMember(study, member);
			studyLikeRepository.delete(studyLike);
		}

		return StudyLikeResponseDto.builder()
			.studyId(studyId)
			.isLiked(isLiked)
			.build();
	}
}
