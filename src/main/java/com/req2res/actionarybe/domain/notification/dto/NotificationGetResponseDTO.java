package com.req2res.actionarybe.domain.notification.dto;

import com.req2res.actionarybe.domain.notification.entity.Notification;
import com.req2res.actionarybe.domain.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationGetResponseDTO {

    @Schema(description = "알림 ID", example = "131")
    private Long notificationId;

    @Schema(description = "알림 유형", example = "COMMENT", allowableValues = {"COMMENT","POINT","DAILY_STUDY_SUMMARY"})
    private NotificationType type;

    @Schema(description = "제목", example = "내 게시글에 댓글이 달렸습니다.")
    private String title;

    @Schema(description = "내용", example = "다현님이 작성한 글에 새로운 댓글이 있어요.")
    private String content;

    @Schema(description = "이동 경로", example = "/posts/77", nullable = true)
    private String link;

    @Schema(description = "읽음 여부", example = "false")
    private Boolean isRead;

    @Schema(description = "생성 시각", example = "2025-10-31T12:55:00")
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
