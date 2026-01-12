package com.req2res.actionarybe.domain.studyTime.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.study.entity.Study;
import com.req2res.actionarybe.domain.study.entity.StudyParticipant;
import com.req2res.actionarybe.domain.study.repository.StudyParticipantRepository;
import com.req2res.actionarybe.domain.study.repository.StudyRepository;
import com.req2res.actionarybe.domain.studyTime.dto.MonthlyDurationSecondsDto;
import com.req2res.actionarybe.domain.studyTime.dto.StudyTimeCalendarResponseDto;
import com.req2res.actionarybe.domain.studyTime.dto.StudyTimeManualRequestDto;
import com.req2res.actionarybe.domain.studyTime.dto.StudyTimeManualResponseDto;
import com.req2res.actionarybe.domain.studyTime.dto.StudyTimePeriodResponseDto;
import com.req2res.actionarybe.domain.studyTime.dto.StudyTimeResponseDto;
import com.req2res.actionarybe.domain.studyTime.dto.StudyTimeTypeRequestDto;
import com.req2res.actionarybe.domain.studyTime.entity.Period;
import com.req2res.actionarybe.domain.studyTime.entity.StudyTime;
import com.req2res.actionarybe.domain.studyTime.entity.StudyTimeManual;
import com.req2res.actionarybe.domain.studyTime.entity.Type;
import com.req2res.actionarybe.domain.studyTime.repository.StudyTimeManualRepository;
import com.req2res.actionarybe.domain.studyTime.repository.StudyTimeRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyTimeService {

	private final StudyTimeRepository studyTimeRepository;
	private final StudyParticipantRepository studyParticipantRepository;
	private final StudyRepository studyRepository;
	private final StudyTimeManualRepository studyTimeManualRepository;

	@Transactional
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
		studyParticipant.updateLastStateChangedAt(LocalDateTime.now());

		return studyTime;
	}

	@Transactional
	public StudyTimeResponseDto createStudyTimeToDto(Member member, Long studyId,
		@Valid StudyTimeTypeRequestDto request) {

		Study study = studyRepository.findById(studyId).
			orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FIND));

		StudyParticipant studyParticipant = studyParticipantRepository.findByStudyAndMemberAndIsActiveTrue(
				study, member)
			.orElseThrow(() -> new CustomException(ErrorCode.STUDY_PARTICIPANT_NOT_JOINED));

		Type nowType = request.getType();

		StudyTime studyTime = createStudyTime(nowType, studyParticipant);

		long totalStudySeconds = studyTimeRepository.sumTotalDurationSeconds(studyParticipant.getId(), Type.STUDY);
		long totalBreakSeconds = studyTimeRepository.sumTotalDurationSeconds(studyParticipant.getId(), Type.BREAK);

		Type changedType = null;
		if (nowType.equals(Type.STUDY)) {
			changedType = Type.BREAK;
		} else if (nowType.equals(Type.BREAK)) {
			changedType = Type.STUDY;
		}

		return StudyTimeResponseDto.builder()
			.studyTimeId(studyTime.getId())
			.studyParticipantId(studyParticipant.getId())
			.studyId(studyId)
			.userId(member.getId())
			.changedType(changedType)
			.changedTypeLabel(changedType.getLabel())
			.totalStudySeconds(totalStudySeconds)
			.totalBreakSeconds(totalBreakSeconds)
			.build();
	}

	public StudyTimeManualResponseDto createStudyTimeManual(Member member, @Valid StudyTimeManualRequestDto request) {

		LocalDate date = request.getDate();
		LocalDate today = LocalDate.now();

		if (date.isAfter(today)) {
			throw new CustomException(ErrorCode.STUDY_TIME_MANUAL_FUTURE_DATE_NOT_ALLOWED);
		}

		StudyTimeManual studyTimeManual = StudyTimeManual.builder()
			.userId(member.getId())
			.manualDate(date)
			.durationSecond(request.getDurationSecond())
			.build();

		studyTimeManualRepository.save(studyTimeManual);

		return StudyTimeManualResponseDto.from(studyTimeManual);
	}

	public StudyTimeCalendarResponseDto getStudyTimeCalender(Member member, YearMonth yearMonth) {

		LocalDate startDate = yearMonth.atDay(1);
		LocalDate endDate = yearMonth.atEndOfMonth();

		List<StudyTime> studyTimeList = studyTimeRepository.findStudyTimeByMember(member, Type.STUDY,
			startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
		List<StudyTimeManual> studyTimeManulList = studyTimeManualRepository.findByUserIdAndManualDateBetween(
			member.getId(), startDate, endDate);

		Map<LocalDate, Long> durationMap = new HashMap<>();

		for (StudyTime st : studyTimeList) {
			LocalDate studyTimeDate = st.getCreatedAt().toLocalDate();
			durationMap.put(studyTimeDate, durationMap.getOrDefault(studyTimeDate, 0L) + st.getDurationSecond());
		}

		for (StudyTimeManual stm : studyTimeManulList) {
			durationMap.put(stm.getManualDate(),
				durationMap.getOrDefault(stm.getManualDate(), 0L) + stm.getDurationSecond());
		}

		List<MonthlyDurationSecondsDto> monthlyDurationSeconds = new ArrayList<>();
		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
			monthlyDurationSeconds.add(MonthlyDurationSecondsDto.builder()
				.date(date)
				.durationSeconds(durationMap.getOrDefault(date, 0L))
				.build());
		}

		return StudyTimeCalendarResponseDto.builder()
			.monthlyDurationSeconds(monthlyDurationSeconds)
			.build();
	}

	public StudyTimePeriodResponseDto getStudyTimePeriod(Member member, LocalDate date, Period period) {

		LocalDate startDate = date;
		LocalDate endDate = date;

		if (period == Period.WEEK) {
			startDate = date.with(java.time.DayOfWeek.MONDAY);
			endDate = date.with(java.time.DayOfWeek.SUNDAY);
		} else if (period == Period.MONTH) {
			startDate = date.withDayOfMonth(1);
			endDate = date.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
		} else if (period == Period.YEAR) {
			startDate = date.withDayOfYear(1);
			endDate = date.with(java.time.temporal.TemporalAdjusters.lastDayOfYear());
		}

		Long durationSeconds = studyTimeRepository.sumTodaySeconds(member.getId(), startDate.atStartOfDay(),
			endDate.plusDays(1).atStartOfDay());
		durationSeconds += studyTimeManualRepository.sumTodaySeconds(member.getId(), startDate, endDate);

		return StudyTimePeriodResponseDto.builder()
			.period(period)
			.date(date)
			.durationSeconds(durationSeconds)
			.build();
	}
}
