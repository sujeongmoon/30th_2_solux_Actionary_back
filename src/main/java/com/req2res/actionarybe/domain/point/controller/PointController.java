package com.req2res.actionarybe.domain.point.controller;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.point.dto.*;
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
    @Operation(
            summary = "스터디 참여 포인트 적립",
            description = """
                사용자가 **스터디에 30분 이상 참여했을 때** 호출하는 API입니다.

                - 참여 시간이 **30분 이상**인 경우에만 포인트가 적립됩니다.
                - 스터디 1개당 **1회만 포인트 지급**됩니다.
                - 포인트 적립 시 **알림이 생성**됩니다.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "스터디 참여 포인트 적립 성공",
                    content = @Content(
                            schema = @Schema(implementation = StudyParticipationPointResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (참여 시간 30분 미만, 필수 값 누락)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (토큰 없음 또는 만료)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 사용자 또는 스터디",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 해당 스터디 참여 포인트를 적립한 경우",
                    content = @Content
            )
    })
    @PostMapping("/study-participation")
    public ResponseEntity<Response<StudyParticipationPointResponseDTO>> earnStudyParticipationPoint(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @RequestBody @Valid StudyParticipationPointRequestDTO request
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        StudyParticipationPointResponseDTO data =
                pointService.earnStudyParticipationPoint(member.getId(), request);

        return ResponseEntity.ok(
                Response.success("스터디 참여로 10P가 적립되었습니다!", data)
        );
    }

    // 3. 투두 완료 포인트 적립 API
    @Operation(
            summary = "투두 완료 포인트 적립",
            description = """
                사용자가 **할 일 1개를 완료했을 때** 호출합니다.

                - 할 일 1개 완료 = **1P**
                - **하루 최대 5P**까지만 지급됩니다.
                - 오늘 이미 5P를 받았다면 **earnedPoint = 0**으로 정상 응답합니다.
                - 포인트 적립 시 **알림이 생성**됩니다.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "투두 완료 포인트 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (필수값 누락 등)", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 없음 또는 만료)", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자/투두", content = @Content),
            @ApiResponse(responseCode = "409", description = "이미 해당 투두 완료 포인트를 적립한 경우", content = @Content)
    })
    @PostMapping("/todos")
    public ResponseEntity<Response<TodoCompletionPointResponseDTO>> earnTodoCompletionPoint(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid TodoCompletionPointRequestDTO request
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        TodoCompletionPointResponseDTO data =
                pointService.earnTodoCompletionPoint(member.getId(), request);

        // earnedPoint가 0이면 한도 안내 메시지
        String msg = (data.getEarnedPoint() == 0)
                ? "오늘 투두 완료 포인트 한도(5P)를 모두 사용했습니다."
                : "할 일 완료로 1P가 적립되었습니다!";

        return ResponseEntity.ok(Response.success(msg, data));
    }


}
