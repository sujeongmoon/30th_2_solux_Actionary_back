package com.req2res.actionarybe.domain.point.entity;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.global.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_point",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_point_member", columnNames = "member_id")
        },
        indexes = {
                @Index(name = "idx_user_point_member_id", columnList = "member_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserPoint extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 포인트 id (PK, auto increment)

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 유저 id (FK, UNIQUE)

    @Column(name = "total_point", nullable = false)
    private int totalPoint; // 총 포인트

    @Column(name = "last_earned_at")
    private LocalDateTime lastEarnedAt; // 최근 적립 일시 (nullable)

    public void addPoint(int earnedPoint, LocalDateTime now) {
        this.totalPoint += earnedPoint;
        this.lastEarnedAt = now;
    }
}