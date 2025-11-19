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
    private Long userId;   // 투두 주인

    @Column(nullable = false)
    private String title;  // 투두 제목

    @Column(nullable = false)
    private LocalDate date;   // 목표 날짜

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING; // 기본값 설정

    @Column(name = "category_id")
    private Long categoryId; // 카테고리 ID (NULL 가능)

    // 상태 enum
    public enum Status {
        PENDING,
        DONE,
        FAILED

    }
}

