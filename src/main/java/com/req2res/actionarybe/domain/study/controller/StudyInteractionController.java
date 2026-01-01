package com.req2res.actionarybe.domain.study.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.service.MemberService;
import com.req2res.actionarybe.domain.study.dto.HitStudyListResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudyLikeResponseDto;
import com.req2res.actionarybe.domain.study.dto.StudyRankingBoardListResponseDto;
import com.req2res.actionarybe.domain.study.service.StudyInteractionService;
import com.req2res.actionarybe.global.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies")
public class StudyInteractionController {

	private final StudyInteractionService studyInteractionService;
	private final MemberService memberService;

	@Operation(summary = "스터디 랭킹 보드 조회 API", description = "스터디 상세 조회에서의 랭킹 보드 조회 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "스터디의 랭킹보드 조회 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "스터디의 랭킹보드가 조회되었습니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "404",
			description = "랭킹보드 조회 하고자 하는 스터디가 없는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 404,
					"message": "존재하지 않는 스터디입니다."
				}
				"""))
		)
	})
	@GetMapping("/{studyId}/rankings")
	public Response<StudyRankingBoardListResponseDto> getStudyRankingBoardList(
		@Parameter(name = "studyId", description = "랭킹보드 조회할 스터디의 ID", example = "1")
		@PathVariable Long studyId,

		@Parameter(description = "일간/누적 참가시간", example = "today")
		@RequestParam(defaultValue = "today") String type
	) {
		StudyRankingBoardListResponseDto response = studyInteractionService.getRankingBoardStudyList(studyId, type);
		return Response.success("스터디의 랭킹보드가 조회되었습니다", response);
	}

	@Operation(summary = "인기 스터디 목록 조회 API", description = "개설돼있는 스터디 중 인기 스터디를 조회하는 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "인기 스터디 목록 조회 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "인기 스터디 목록을 조회했습니다."
				}
				"""))
		)
	})
	@GetMapping("/hit")
	public Response<HitStudyListResponseDto> getHitStudyList(
		@Parameter(description = "페이지", example = "0")
		@RequestParam(defaultValue = "0") int page
	) {
		HitStudyListResponseDto response = studyInteractionService.getHitStudyList(page);
		return Response.success("인기 스터디 목록을 조회했습니다.", response);
	}

	@Operation(summary = "스터디 즐겨찾기 성공/취소 API", description = "스터디 상세 조회에서의 즐겨찾기/취소 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "스터디가 즐겨찾기 성공/취소 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "해당 스터디가 즐겨찾기 성공/취소 되었습니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "404",
			description = "즐겨찾기 성공/취소 하고자 하는 스터디가 없는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 404,
					"message": "존재하지 않는 스터디입니다."
				}
				"""))
		)
	})
	@PostMapping("/{studyId}/likes")
	public Response<StudyLikeResponseDto> createStudyLike(
		@AuthenticationPrincipal UserDetails userDetails,
		@Parameter(name = "studyId", description = "즐겨찾기 성공/취소할 스터디의 ID", example = "1")
		@PathVariable Long studyId
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		StudyLikeResponseDto response = studyInteractionService.createStudyLike(member, studyId);
		return Response.success("해당 스터디가 즐겨찾기 성공/취소 되었습니다.", response);
	}
}
