package com.req2res.actionarybe.domain.notification.dto;
// 알림 생성할 때 쓰는 request dto

import com.req2res.actionarybe.domain.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class NotificationCreateRequestDTO {

    @Schema(
            description = "알림을 받을 사용자 ID",
            example = "1"
    )
    @NotNull(message = "receiverId는 필수입니다.")
    private Long receiverId;

    @Schema(
            description = "알림 유형",
            example = "COMMENT",
            allowableValues = {"COMMENT","POINT","DAILY_STUDY_SUMMARY"}
    )
    @NotNull(message = "type은 필수입니다.")
    private NotificationType type;

    @Schema(
            description = "알림 제목",
            example = "내 게시글에 댓글이 달렸습니다."
    )
    @NotBlank(message = "title은 필수입니다.")
    private String title;

    @Schema(
            description = "알림 상세 내용",
            example = "다현님이 댓글을 남겼어요."
    )
    private String content;

    @Schema(
            description = """
            알림 클릭 시 이동 경로  
            - COMMENT 타입만 사용  
            - 그 외 타입은 null
            """,
            example = "/posts/25",
            nullable = true
    )
    private String link;
}
