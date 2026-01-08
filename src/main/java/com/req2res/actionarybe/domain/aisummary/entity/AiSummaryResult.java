package com.req2res.actionarybe.domain.aisummary.entity;

import com.req2res.actionarybe.global.Timestamped;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "ai_summary_result",
        indexes = {
                @Index(name = "idx_ai_summary_result_job_id", columnList = "job_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiSummaryResult extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * jobId로 조인하기 (FK + UNIQUE 권장)
     * - DB에서는 ai_summary_job.job_id 를 참조하는 FK
     * - JPA에서는 단순 문자열로 두고, 필요하면 서비스에서 jobId로 찾는 게 가장 단순
     */
    @Column(name = "job_id", nullable = false, unique = true, length = 50)
    private String jobId;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String summary; // 전체 요약 본문
}
