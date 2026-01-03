package com.req2res.actionarybe.domain.point.controller;

import com.req2res.actionarybe.domain.point.dto.PublicUserPointResponseDTO;
import com.req2res.actionarybe.domain.point.service.UserPointService;
import com.req2res.actionarybe.global.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserPointController {

    private final UserPointService userPointService;

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
}
