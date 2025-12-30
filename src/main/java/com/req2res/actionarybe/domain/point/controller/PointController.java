package com.req2res.actionarybe.domain.point.controller;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.point.dto.StudyParticipationPointRequestDTO;
import com.req2res.actionarybe.domain.point.dto.StudyParticipationPointResponseDTO;
import com.req2res.actionarybe.domain.point.dto.StudyTimePointRequestDTO;
import com.req2res.actionarybe.domain.point.dto.StudyTimePointResponseDTO;
import com.req2res.actionarybe.domain.point.service.PointService;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;
    private final MemberRepository memberRepository;

    // 1. 공부시간 포인트 적립 API
    @Operation(
            summary = "공부시간 포인트 적립",
            description = """
                    사용자가 **당일 공부한 시간(studyHours)** 을 전송하면  
                    `공부시간 × 10` 만큼 포인트를 적립합니다.

                    - 하루 1회만 적립 가능
                    - 포인트 적립 시 알림이 생성됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "공부시간 포인트 적립 성공",
                    content = @Content(schema = @Schema(implementation = StudyTimePointResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (필수값 누락, studyHours ≤ 0 등)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (토큰 없음 또는 만료)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 사용자",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "오늘 이미 공부시간 포인트를 적립한 경우",
                    content = @Content
            )
    })


    @PostMapping("/study-time")
    public ResponseEntity<Response<StudyTimePointResponseDTO>> earnStudyTimePoint(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @RequestBody @Valid StudyTimePointRequestDTO request
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = member.getId();

        StudyTimePointResponseDTO data = pointService.earnStudyTimePoint(userId, request);

        String msg = "공부시간 " + request.getStudyHours() + "시간 기록으로 "
                + data.getEarnedPoint() + "P가 적립되었습니다.";

        return ResponseEntity.ok(Response.success(msg, data));
    }

    // 2. 스터디 참여 포인트 적립 API
    @PostMapping("/study-participation")
    public ResponseEntity<Response<StudyParticipationPointResponseDTO>> earnStudyParticipationPoint(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid StudyParticipationPointRequestDTO request
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        StudyParticipationPointResponseDTO data =
                pointService.earnStudyParticipationPoint(member.getId(), request);

        return ResponseEntity.ok(Response.success("스터디 참여로 10P가 적립되었습니다!", data));
    }

}
