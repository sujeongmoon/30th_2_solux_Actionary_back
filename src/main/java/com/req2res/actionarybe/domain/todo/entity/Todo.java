package com.req2res.actionarybe.domain.todo.entity;

import com.req2res.actionarybe.global.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "todo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;   // 두두 주인

    @Column(nullable = false)
    private String title;  // 두두 제목

    @Column(nullable = false)
    private LocalDate date;   // 목표 날짜

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING; // 기본값 설정

    @Column(name = "category_id")
    private Long categoryId; // 카테고리 ID (NULL 가능)

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;   // 삭제 여부

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;     // 삭제 시각(옵션)

    // ====== Soft Delete 메서드 ======

    //소프트 삭제 처리
    public void softDelete() {
        if (this.isDeleted) {
            return; // 이미 삭제된 경우는 그냥 무시
        }
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    //삭제 취소(복구)하고 싶을 때 사용 가능
    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    // 상태 enum
    public enum Status {
        PENDING,
        DONE,
        FAILED

    }
}

