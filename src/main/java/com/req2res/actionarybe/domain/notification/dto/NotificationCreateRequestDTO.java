package com.req2res.actionarybe.domain.notification.dto;
//알림 생성할 때 쓰는 request dto


import com.req2res.actionarybe.domain.notification.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class NotificationCreateRequestDTO {

    @NotNull(message = "receiverId는 필수입니다.")
    private Long receiverId;

    @NotNull(message = "type은 필수입니다.")
    private NotificationType type;

    @NotBlank(message = "title은 필수입니다.")
    private String title;

    private String content;
    private String link;
}
