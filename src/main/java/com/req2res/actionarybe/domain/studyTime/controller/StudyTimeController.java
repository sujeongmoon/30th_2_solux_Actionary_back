package com.req2res.actionarybe.domain.studyTime.controller;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.service.MemberService;
import com.req2res.actionarybe.domain.studyTime.dto.StudyTimeCalendarResponseDto;
import com.req2res.actionarybe.domain.studyTime.dto.StudyTimeManualRequestDto;
import com.req2res.actionarybe.domain.studyTime.dto.StudyTimeManualResponseDto;
import com.req2res.actionarybe.domain.studyTime.dto.StudyTimePeriodResponseDto;
import com.req2res.actionarybe.domain.studyTime.entity.Period;
import com.req2res.actionarybe.domain.studyTime.service.StudyTimeService;
import com.req2res.actionarybe.global.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studytimes")
public class StudyTimeController {

	private final StudyTimeService studyTimeService;
	private final MemberService memberService;

	@Operation(summary = "수동 공부량 추가 API", description = "수동으로 공부량을 추가하는 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "수동 공부량 추가 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "수동으로 공부량을 추가했습니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "400",
			description = "미래의 날짜에 공부량 등록 요청",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 400,
					"message": "미래 날짜에는 공부 시간을 수동으로 추가할 수 없습니다."
				}
				"""))
		)
	})
	@PostMapping
	public Response<StudyTimeManualResponseDto> createStudyTimeManual(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody @Valid StudyTimeManualRequestDto request
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		StudyTimeManualResponseDto response = studyTimeService.createStudyTimeManual(member, request);
		return Response.success("수동으로 공부량을 추가했습니다.", response);
	}

	@Operation(summary = "공부량 달력 월별 일간 조회 API", description = "월간 공부 시간 조회 내역입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "월간 공부 시간 조회 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "월간 공부 시간 조회 내역입니다."
				}
				"""))
		)
	})
	@GetMapping("/calender")
	public Response<StudyTimeCalendarResponseDto> getStudyTimeCalender(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam YearMonth date
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		StudyTimeCalendarResponseDto response = studyTimeService.getStudyTimeCalender(member, date);
		return Response.success("월간 공부 시간 조회 내역입니다.", response);
	}

	@Operation(summary = "공부량 일간/주간/월간/연간 조회 API", description = "월일간/주간/월간/연간 공부량을 조회하는 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "공부 시간 조회 내역입니다.",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "공부 시간 조회 내역입니다."
				}
				"""))
		)
	})
	@GetMapping
	public Response<StudyTimePeriodResponseDto> getStudyTimePeriod(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam LocalDate date,
		@RequestParam Period period
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		StudyTimePeriodResponseDto response = studyTimeService.getStudyTimePeriod(member, date, period);
		return Response.success("공부 시간 조회 내역입니다.", response);
	}

}
