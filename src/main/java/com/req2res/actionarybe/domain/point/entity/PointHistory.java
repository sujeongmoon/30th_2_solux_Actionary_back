package com.req2res.actionarybe.domain.point.entity;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.global.Timestamped;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "point_history",
        indexes = {
                @Index(name = "idx_point_history_member_created", columnList = "member_id, createdAt"),
                @Index(name = "idx_point_history_source", columnList = "source")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PointHistory extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 포인트 내역 id (PK)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 유저 id (FK)

    @Column(name = "study_room_id")
    private Long studyRoomId;

    @Column(name = "earned_point", nullable = false)
    private int earnedPoint; // 이번에 적립된 포인트

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private PointSource source; // STUDY_TIME / STUDY_PARTICIPATION / TODO_COMPLETION

    @Column(name = "total_point", nullable = false)
    private int totalPoint; // 적립 후 총 포인트 (스냅샷)
}