package com.req2res.actionarybe.domain.notification.dto;

import com.req2res.actionarybe.domain.notification.entity.Notification;
import com.req2res.actionarybe.domain.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationGetResponseDTO {

    private Long notificationId;
    private NotificationType type;
    private String title;
    private String content;
    private String link;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationGetResponseDTO from(Notification n) {
        return NotificationGetResponseDTO.builder()
                .notificationId(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .content(n.getContent())
                .link(n.getLink())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
