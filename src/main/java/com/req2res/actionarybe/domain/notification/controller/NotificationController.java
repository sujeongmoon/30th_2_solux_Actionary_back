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

    // 1. 알림 생성 API
    @PostMapping
    public ResponseEntity<Response<NotificationCreateResponseDTO>> createNotification(
            @RequestBody @Valid NotificationCreateRequestDTO request
    ) {
        NotificationCreateResponseDTO data = notificationService.create(request);
        return ResponseEntity.ok(Response.success("알림이 생성되었습니다.", data));
    }

    // 2. 알림 조회 API
    @GetMapping
    public ResponseEntity<Response<List<NotificationGetResponseDTO>>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer limit
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = member.getId();

        List<NotificationGetResponseDTO> data = notificationService.getMyNotifications(userId, limit);

        return ResponseEntity.ok(Response.success("", data));
    }

}
