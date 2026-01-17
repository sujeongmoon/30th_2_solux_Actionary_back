package com.req2res.actionarybe.domain.notification.entity;

import com.req2res.actionarybe.global.Timestamped;
import com.req2res.actionarybe.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notification")
public class Notification extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 받는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column
    private String content;

    @Column
    private String link;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "read_at")
    private java.time.LocalDateTime readAt;

    public static Notification create(Member receiver, NotificationType type,
                                      String title, String content, String link) {
        return Notification.builder()
                .receiver(receiver)
                .type(type)
                .title(title)
                .content(content)
                .link(link)
                .isRead(false)
                .readAt(null)
                .build();
    }

    public void markAsRead() {
        if (Boolean.TRUE.equals(this.isRead)) {
            return; // 멱등성
        }
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

}
