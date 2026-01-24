package com.req2res.actionarybe.domain.notification.controller;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.notification.dto.NotificationCreateRequestDTO;
import com.req2res.actionarybe.domain.notification.dto.NotificationCreateResponseDTO;
import com.req2res.actionarybe.domain.notification.dto.NotificationGetResponseDTO;
import com.req2res.actionarybe.domain.notification.service.NotificationService;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final MemberRepository memberRepository;


    @Operation(
            summary = "알림 생성 API",
            description = """
                    서비스에서 저장해야 하는 알림(댓글/포인트/투두 올클리어/자정 공부량)을 생성합니다.
                    생성 시 isRead는 기본 false로 저장됩니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "알림 생성 완료",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "message": "알림이 생성되었습니다.",
                              "data": {
                                "notificationId": 101,
                                "receiverId": 7,
                                "type": "POINT",
                                "title": "포인트가 적립되었습니다.",
                                "content": "공부시간 기록으로 100P가 적립되었어요.",
                                "link": "/mypage/points",
                                "isRead": false,
                                "createdAt": "2025-10-31T12:30:00"
                              }
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (receiverId/type/title 누락 등)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": false,
                              "message": "잘못된 요청입니다."
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패(토큰 없음/만료)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": false,
                              "message": "인증이 필요합니다."
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "receiverId에 해당하는 사용자가 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": false,
                              "message": "리소스를 찾을 수 없습니다."
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류(알림 저장 중 예외 발생)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": false,
                              "message": "서버 오류가 발생했습니다."
                            }
                            """))
            )
    })
    // 1. 알림 생성 API
    @PostMapping
    public ResponseEntity<Response<NotificationCreateResponseDTO>> createNotification(
            @RequestBody @Valid NotificationCreateRequestDTO request
    ) {
        NotificationCreateResponseDTO data = notificationService.create(request);
        return ResponseEntity.ok(Response.success("알림이 생성되었습니다.", data));
    }


    @Operation(
            summary = "알림 목록 조회 API",
            description = """
                    내 알림 목록을 최신순(createdAt DESC)으로 조회합니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "알림 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "message": "",
                              "data": [
                                {
                                  "notificationId": 131,
                                  "type": "COMMENT",
                                  "title": "내 게시글에 댓글이 달렸습니다.",
                                  "content": "다현님이 작성한 글에 새로운 댓글이 있어요.",
                                  "link": "/posts/77",
                                  "isRead": false,
                                  "createdAt": "2025-10-31T12:55:00"
                                },
                                {
                                  "notificationId": 130,
                                  "type": "POINT",
                                  "title": "포인트가 적립되었습니다.",
                                  "content": "스터디 참여로 10P 적립!",
                                  "link": "/mypage/points",
                                  "isRead": true,
                                  "createdAt": "2025-10-31T12:40:00"
                                }
                              ]
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패(토큰 없음/만료)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": false,
                              "message": "인증이 필요합니다."
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류(알림 목록 조회 중 예외 발생)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": false,
                              "message": "서버 오류가 발생했습니다."
                            }
                            """))
            )
    })

    // 2. 알림 조회 API
    @GetMapping
    public ResponseEntity<Response<List<NotificationGetResponseDTO>>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = member.getId();

        List<NotificationGetResponseDTO> data =
                notificationService.getMyNotifications(userId);

        return ResponseEntity.ok(Response.success("", data));
    }


    // 3. 알림 읽음 처리 API
    @Operation(
            summary = "알림 단건 읽음 처리 API",
            description = """
                알림을 클릭했을 때 해당 알림의 isRead를 true로 변경합니다.
                내 알림만 읽음 처리 가능하며, 이미 읽음인 경우에도 200 OK로 응답합니다.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패(토큰 없음/만료)", content = @Content),
            @ApiResponse(responseCode = "403", description = "내 알림이 아님", content = @Content),
            @ApiResponse(responseCode = "404", description = "알림 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Response<NotificationGetResponseDTO>> readNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long notificationId
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        NotificationGetResponseDTO data = notificationService.markAsRead(member.getId(), notificationId);

        return ResponseEntity.ok(Response.success("알림을 읽음 처리했습니다.", data));
    }

}
