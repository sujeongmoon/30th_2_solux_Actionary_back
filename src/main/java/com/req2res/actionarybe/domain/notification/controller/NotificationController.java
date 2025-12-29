package com.req2res.actionarybe.domain.notification.controller;

import com.req2res.actionarybe.domain.notification.dto.NotificationCreateRequestDTO;
import com.req2res.actionarybe.domain.notification.dto.NotificationCreateResponseDTO;
import com.req2res.actionarybe.domain.notification.service.NotificationService;
import com.req2res.actionarybe.global.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Response<NotificationCreateResponseDTO>> createNotification(
            @RequestBody @Valid NotificationCreateRequestDTO request
    ) {
        NotificationCreateResponseDTO data = notificationService.create(request);
        return ResponseEntity.ok(Response.success("알림이 생성되었습니다.", data));
    }
}
