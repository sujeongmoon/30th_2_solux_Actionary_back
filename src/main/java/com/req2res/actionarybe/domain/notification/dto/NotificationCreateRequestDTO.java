package com.req2res.actionarybe.domain.notification.dto;
//알림 생성할 때 쓰는 request dto

import com.req2res.actionarybe.domain.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class NotificationCreateRequestDTO {

    @Schema(description = "알림을 받을 사용자 ID", example = "7")
    @NotNull(message = "receiverId는 필수입니다.")
    private Long receiverId;

    @Schema(description = "알림 유형", example = "POINT", allowableValues = {"COMMENT","POINT","TODO_ALL_DONE","DAILY_STUDY_SUMMARY"})
    @NotNull(message = "type은 필수입니다.")
    private NotificationType type;

    @Schema(description = "알림 제목", example = "포인트가 적립되었습니다.")
    @NotBlank(message = "title은 필수입니다.")
    private String title;

    @Schema(description = "알림 상세 내용", example = "공부시간 기록으로 100P가 적립되었어요.")
    private String content;

    @Schema(description = "알림 클릭 시 이동 경로(없으면 null)", example = "/mypage/points", nullable = true)
    private String link;
}
