package com.req2res.actionarybe.domain.study.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.service.MemberService;
import com.req2res.actionarybe.domain.study.dto.StudyDetailResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudyListResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudyRequestDto;
import com.req2res.actionarybe.domain.study.dto.StudyResponseDto;
import com.req2res.actionarybe.domain.study.entity.Category;
import com.req2res.actionarybe.domain.study.service.StudyService;
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
@RequestMapping("/api/studies")
public class StudyController {

	private final StudyService studyService;
	private final MemberService memberService;

	@Operation(summary = "스터디 생성 API", description = "스터디를 생성하는 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "스터디 생성 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "스터디가 생성되었습니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "404",
			description = "북마크 링크가 비어있는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 404,
					"message": "북마크 링크는 비어있을 수 없습니다."
				}
				"""))
		)
	})
	@PostMapping
	public Response<StudyResponseDto> createStudy(
		// TODO: 추후 pull 후 getMemberIdFromToken으로 memberId 불러온 뒤 전달할 수 있도록 수정
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody @Valid StudyRequestDto request
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		StudyResponseDto response = studyService.createStudy(member, request);
		return Response.success("스터디가 생성되었습니다.", response);
	}

	@Operation(summary = "스터디 삭제 API", description = "스터디를 삭제하는 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "스터디 삭제 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "스터디를 삭제했습니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "403",
			description = "생성자와 요청자의 불일치로 접근권한 없는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 403,
					"message": "사용자가 방장인 스터디가 아닙니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "404",
			description = "삭제하고자 하는 스터디가 없는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 404,
					"message": "존재하지 않는 스터디입니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "409",
			description = "스터디에 참여 중인 유저가 존재하는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 409,
					"message": "스터디에 참여 중인 사용자가 있습니다."
				}
				"""))
		)
	})
	@DeleteMapping("/{studyId}")
	public Response<StudyResponseDto> deleteStudy(
		// TODO: 추후 pull 후 getMemberIdFromToken으로 memberId 불러온 뒤 전달할 수 있도록 수정
		@AuthenticationPrincipal UserDetails userDetails,
		@Parameter(name = "studyId", description = "조회할 스터디의 ID", example = "1")
		@PathVariable Long studyId
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		studyService.deleteStudy(member, studyId);
		return Response.success("스터디가 삭제되었습니다.", null);
	}

	@Operation(summary = "스터디 수정 API", description = "스터디를 수정하는 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "스터디 수정 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "스터디를 수정했습니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "403",
			description = "생성자와 요청자의 불일치로 접근권한 없는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 403,
					"message": "사용자가 방장인 스터디가 아닙니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "404",
			description = "삭제하고자 하는 스터디가 없는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 404,
					"message": "존재하지 않는 스터디입니다."
				}
				"""))
		)
	})
	@PutMapping("/{studyId}")
	public Response<StudyResponseDto> updateStudy(
		// TODO: 추후 pull 후 getMemberIdFromToken으로 memberId 불러온 뒤 전달할 수 있도록 수정
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody @Valid StudyRequestDto request,
		@Parameter(name = "studyId", description = "수정할 스터디의 ID", example = "1")
		@PathVariable Long studyId
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		StudyResponseDto response = studyService.updateStudy(member, request, studyId);
		return Response.success("스터디가 수정되었습니다.", response);
	}

	@Operation(summary = "스터디 상세 조회 API", description = "스터디의 썸네일 및 제목을 클릭했을 시 보여지는 스터디 상세 조회 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "스터디 상세 조회 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "스터디가 상세 조회되었습니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "404",
			description = "상세 조회 하고자 하는 스터디가 없는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 404,
					"message": "존재하지 않는 스터디입니다."
				}
				"""))
		)
	})
	@GetMapping("/{studyId}")
	public Response<StudyDetailResponseDto> getStudyDetail(
		// TODO: 추후 pull 후 getMemberIdFromToken으로 memberId 불러온 뒤 전달할 수 있도록 수정
		@AuthenticationPrincipal UserDetails userDetails,
		@Parameter(name = "studyId", description = "상세 조회할 스터디의 ID", example = "1")
		@PathVariable Long studyId
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		StudyDetailResponseDto response = studyService.getStudyDetail(member, studyId);
		return Response.success("스터디가 상세 조회되었습니다.", response);
	}

	@Operation(summary = "스터디 목록 조회 API", description = "개설돼있는 전체 스터디를 분류에 따라 조회하는 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "스터디 목록 조회 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "스터디 리스트를 조회했습니다."
				}
				"""))
		)
	})
	@GetMapping
	public Response<StudyListResponseDto> getStudyList(
		@Parameter(description = "스터디 공개 여부", example = "public")
		@RequestParam(defaultValue = "public") String visibility,

		@Parameter(description = "스터디 카테고리", example = "TEACHER_EXAM")
		@RequestParam(required = false) Category category,

		@Parameter(description = "페이지", example = "0")
		@RequestParam(defaultValue = "0") int pageNumber
	) {
		StudyListResponseDto response = studyService.getStudyList(visibility, category, pageNumber);
		return Response.success("스터디 리스트를 조회했습니다.", response);
	}

}
