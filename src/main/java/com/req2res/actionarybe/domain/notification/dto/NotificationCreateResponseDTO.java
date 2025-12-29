package com.req2res.actionarybe.domain.notification.dto;
//알림 생성 response DTO

import com.req2res.actionarybe.domain.notification.entity.Notification;
import com.req2res.actionarybe.domain.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationCreateResponseDTO {

    private Long notificationId;
    private Long receiverId;
    private NotificationType type;
    private String title;
    private String content;
    private String link;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationCreateResponseDTO from(Notification n) {
        return NotificationCreateResponseDTO.builder()
                .notificationId(n.getId())
                .receiverId(n.getReceiver().getId())
                .type(n.getType())
                .title(n.getTitle())
                .content(n.getContent())
                .link(n.getLink())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
