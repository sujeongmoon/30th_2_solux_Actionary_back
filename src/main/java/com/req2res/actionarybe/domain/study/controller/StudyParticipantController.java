package com.req2res.actionarybe.domain.study.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.service.MemberService;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantNowStateRequestDto;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantNowStateResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantPrivateRequestDto;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantUsersResponseDto;
import com.req2res.actionarybe.domain.study.service.StudyParticipantService;
import com.req2res.actionarybe.global.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/participating")
public class StudyParticipantController {

	private final StudyParticipantService studyParticipantService;
	private final MemberService memberService;

	@Operation(summary = "공개 스터디 접속 API", description = "공개 스터디를 접속 하는 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "공개 스터디 접속 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "공개 스터디에 접속했습니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "404",
			description = "참여하고자 하는 스터디가 없는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 404,
					"message": "존재하지 않는 스터디입니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "409",
			description = "스터디 참여 인원이 정원에 도달한 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 409,
					"message": "스터디 참여 인원이 이미 정원에 도달했습니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "403",
			description = "비공개 스터디에 공개 스터디 입장 API를 요청한 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 403,
					"message": "비공개 스터디 입장 시 비밀번호가 필요합니다."
				}
				"""))
		)
	})
	@PostMapping("/public")
	public Response<StudyParticipantResponseDto> createStudyParticipantPublic(
		@AuthenticationPrincipal UserDetails userDetails,
		@Parameter(name = "studyId", description = "참여할 스터디의 ID", example = "1")
		@PathVariable Long studyId
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		StudyParticipantResponseDto response = studyParticipantService.createStudyParticipantPublic(member, studyId);
		return Response.success("공개 스터디에 접속했습니다.", response);
	}

	@Operation(summary = "비공개 스터디 접속 API", description = "비공개 스터디를 접속 하는 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "비공개 스터디 접속 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "비공개 스터디에 접속했습니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "404",
			description = "참여하고자 하는 스터디가 없는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 404,
					"message": "존재하지 않는 스터디입니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "409",
			description = "스터디 참여 인원이 정원에 도달한 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 409,
					"message": "스터디 참여 인원이 이미 정원에 도달했습니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "400",
			description = "공개 스터디에 비공개 스터디 입장 API를 요청한 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 400,
					"message": "공개 스터디 입장 시 비밀번호가 필요하지 않습니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "401",
			description = "비공개 스터디 입장 비밀번호가 일치하지 않는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 401,
					"message": "비공개 스터디 입장 비밀번호가 일치하지 않습니다."
				}
				"""))
		)
	})
	@PostMapping("/private")
	public Response<StudyParticipantResponseDto> createStudyParticipantPrivate(
		@AuthenticationPrincipal UserDetails userDetails,
		@Parameter(name = "studyId", description = "참여할 스터디의 ID", example = "1")
		@PathVariable Long studyId,
		@RequestBody @Valid StudyParticipantPrivateRequestDto request
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		StudyParticipantResponseDto response = studyParticipantService.createStudyParticipantPrivate(member, studyId,
			request);
		return Response.success("비공개 스터디에 접속했습니다.", response);
	}

	@Operation(summary = "스터디 접속 유저 기본정보 표시 API", description = "스터디 접속 시 페이지에서 스터디에 접속해있는 유저들의 기본 정보를 보여주는 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "스터디에 접속한 유저들의 정보 조회 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "스터디에 접속한 유저들의 정보가 조회되었습니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "404",
			description = "참여하고자 하는 스터디가 없는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 404,
					"message": "존재하지 않는 스터디입니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "403",
			description = "유저가 해당 스터디에 참여하고 있지 않은 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 403,
					"message": "유저가 해당 스터디에 참여하고 있지 않습니다."
				}
				"""))
		)
	})
	@GetMapping("/users")
	public Response<StudyParticipantUsersResponseDto> getStudyParticipantUsers(
		@AuthenticationPrincipal UserDetails userDetails,
		@Parameter(name = "studyId", description = "참여할 스터디의 ID", example = "1")
		@PathVariable Long studyId
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		StudyParticipantUsersResponseDto response = studyParticipantService.getStudyParticipantUsers(member, studyId);
		return Response.success("스터디에 접속한 유저들의 정보가 조회되었습니다.", response);
	}

	@Operation(summary = "스터디 접속 시 본인의 말풍선 변경 API", description = "스터디 접속 시 본인의 말풍선을 변경하는 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "스터디 접속 시 본인의 말풍선 변경 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "해당 유저의 말풍선 변경이 완료되었습니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "404",
			description = "참여하고자 하는 스터디가 없는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 404,
					"message": "존재하지 않는 스터디입니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "403",
			description = "유저가 해당 스터디에 참여하고 있지 않은 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 403,
					"message": "유저가 해당 스터디에 참여하고 있지 않습니다."
				}
				"""))
		)
	})
	@PatchMapping("/nowState")
	public Response<StudyParticipantNowStateResponseDto> updateStudyParticipantNowState(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody @Valid StudyParticipantNowStateRequestDto request,
		@Parameter(name = "studyId", description = "참여한 스터디의 ID", example = "1")
		@PathVariable Long studyId
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		StudyParticipantNowStateResponseDto response = studyParticipantService.updateStudyParticipantNowState(request,
			member,
			studyId);
		return Response.success("해당 유저의 말풍선 변경이 완료되었습니다.", response);
	}

}
