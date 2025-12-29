package com.req2res.actionarybe.domain.notification.dto;
//알림 생성 response DTO

import com.req2res.actionarybe.domain.notification.entity.Notification;
import com.req2res.actionarybe.domain.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationCreateResponseDTO {

    @Schema(description = "생성된 알림 ID", example = "101")
    private Long notificationId;

    @Schema(description = "알림을 받는 사용자 ID", example = "7")
    private Long receiverId;

    @Schema(description = "알림 유형", example = "POINT")
    private NotificationType type;

    @Schema(description = "알림 제목", example = "포인트가 적립되었습니다.")
    private String title;

    @Schema(description = "알림 상세 내용", example = "공부시간 기록으로 100P가 적립되었어요.")
    private String content;

    @Schema(description = "이동 경로", example = "/mypage/points", nullable = true)
    private String link;

    @Schema(description = "읽음 여부(기본 false)", example = "false")
    private Boolean isRead;

    @Schema(description = "생성 시각", example = "2025-10-31T12:30:00")
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
