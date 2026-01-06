package com.req2res.actionarybe.domain.point.controller;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.point.dto.MyPointSummaryResponseDTO;
import com.req2res.actionarybe.domain.point.dto.PublicUserPointResponseDTO;
import com.req2res.actionarybe.domain.point.service.UserPointService;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserPointController {

    private final UserPointService userPointService;
    private final MemberRepository memberRepository;

    // 1. 공개용 포인트 조회 API
    @Operation(
            summary = "공개용 포인트 조회",
            description = """
                    다른 사용자의 공개 프로필에서 포인트/업적 정보를 조회하는 API입니다. (permitAll)

                    - 공부시간 포인트(STUDY_TIME)
                    - 스터디 참여 포인트(STUDY_PARTICIPATION)
                    - 투두 완료 포인트(TODO_COMPLETION)
                    - 총 포인트(total)
                    - 보유 배지 목록(badges)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "공개용 포인트 조회 성공",
                    content = @Content(schema = @Schema(implementation = PublicUserPointResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 userId",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 (포인트 계산 오류 등)",
                    content = @Content
            )
    })
    @GetMapping("/{userId}/points")
    public ResponseEntity<Response<PublicUserPointResponseDTO>> getPublicUserPoints(
            @PathVariable Long userId
    ) {
        PublicUserPointResponseDTO data = userPointService.getPublicUserPoints(userId);
        return ResponseEntity.ok(Response.success("", data));
    }

    // 2. 사이드바용 포인트 조회 API
    @Operation(
            summary = "사이드바용 포인트 조회",
            description = """
                    로그인한 사용자의 **누적 포인트(totalPoint)** 를 빠르게 조회합니다.

                    - Authorization 헤더 필수
                    - 사이드바에서 빠르게 사용하기 위한 경량 API
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "사이드바용 포인트 조회 성공",
                    content = @Content(schema = @Schema(implementation = MyPointSummaryResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (토큰 없음/만료)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "접근 거부 (비활성화 사용자 등)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content
            )
    })
    @GetMapping("/me/points")
    public ResponseEntity<Response<MyPointSummaryResponseDTO>> getMyPoints(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        MyPointSummaryResponseDTO data = userPointService.getMyPointSummary(member.getId());

        return ResponseEntity.ok(Response.success("", data));
    }
}
