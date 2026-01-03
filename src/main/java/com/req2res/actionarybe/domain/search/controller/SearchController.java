package com.req2res.actionarybe.domain.search.controller;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.search.dto.StudySearchPageResponseDTO;
import com.req2res.actionarybe.domain.search.service.SearchService;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;
    private final MemberRepository memberRepository;


    // 1. 스터디 검색 API
    @Operation(
            summary = "스터디 검색 결과 조회",
            description = """
                        검색어(q)를 기반으로 스터디를 조회합니다.  
                        - 검색 대상: 스터디 제목(name), 카테고리(category), 스터디 설명(description)
                        - 정렬: RECENT(기본), POPULAR(즐겨찾기 수 기준)
                        - 인증: permitAll (Authorization 헤더는 선택)
                        - 로그인 상태라면 isJoined(참여 여부)를 계산하여 내려줍니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "검색 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Response.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (q 누락 또는 정렬/페이지 값 오류)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (토큰이 만료/위조인데 인증이 필요한 요청일 때)"),
            @ApiResponse(responseCode = "404", description = "검색 결과 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/studies")
    public ResponseEntity<Response<StudySearchPageResponseDTO>> searchStudies(
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "검색어(필수)", example = "임용")
            @RequestParam(name = "q") String q,

            @Parameter(description = "정렬 기준 (RECENT | POPULAR), 기본 RECENT", example = "RECENT")
            @RequestParam(name = "sort", required = false) String sort,

            @Parameter(description = "페이지 번호(1부터 시작), 기본 1", example = "1")
            @RequestParam(name = "page", required = false) Integer page,

            @Parameter(description = "페이지당 결과 수, 기본 10", example = "10")
            @RequestParam(name = "size", required = false) Integer size,

            @RequestHeader(name = "Authorization", required = false) String authorization
    ) {
        Long userId = null;

        // 비로그인(permitAll)인 경우 userDetails는 null일 수 있음
        if (userDetails != null) {
            String loginId = userDetails.getUsername();

            Member member = memberRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

            userId = member.getId();
        }

        StudySearchPageResponseDTO data =
                searchService.searchStudies(q, sort, page, size, userId);

        return ResponseEntity.ok(Response.success("스터디 검색 결과입니다.", data));
    }
}
