package com.req2res.actionarybe.domain.study.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.service.MemberService;
import com.req2res.actionarybe.domain.study.dto.StudyRequestDto;
import com.req2res.actionarybe.domain.study.dto.StudyResponseDto;
import com.req2res.actionarybe.domain.study.service.StudyService;
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

}
