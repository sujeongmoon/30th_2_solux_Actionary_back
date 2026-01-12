package com.req2res.actionarybe.domain.aisummary.entity;

import com.req2res.actionarybe.global.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static com.req2res.actionarybe.domain.aisummary.entity.AiSummaryEnums.*;

@Entity
@Table(
        name = "ai_summary_job",
        indexes = {
                @Index(name = "idx_ai_summary_job_user_id", columnList = "user_id"),
                @Index(name = "idx_ai_summary_job_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiSummaryJob extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 내부 PK

    @Column(name = "user_id")
    private Long userId; // 비회원 요청이면 NULL

    @Column(name = "job_id", nullable = false, unique = true, length = 50)
    private String jobId; // 외부 노출용 jobId (sb_xxxxxx)

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 10)
    private SourceType sourceType; // FILE / URL

    @Column(nullable = false, length = 255)
    private String title; // 파일명/URL 기반 제목

    @Column(name = "file_name", length = 255)
    private String fileName; // sourceType=FILE일 때

    @Column(name = "file_path", length = 500)
    private String filePath; // 실제 저장 경로 or S3 key

    @Column(name = "source_url", length = 1000)
    private String sourceUrl; // sourceType=URL일 때

    @Column(nullable = false, length = 10)
    private String language = "ko";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Status status = Status.PENDING; // PENDING/RUNNING/SUCCEEDED/FAILED

    @Column(name = "has_full_summary", nullable = false)
    private boolean hasFullSummary = false;

    @Column(name = "queued_at")
    private LocalDateTime queuedAt; // 비동기 큐 진입 시간

    @Column(name = "finished_at")
    private LocalDateTime finishedAt; // 완료 시간


    @Column(name = "error_code", length = 100)
    private String errorCode;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    /* ===== 편의 메서드 ===== */

    public void markQueued(LocalDateTime now) {
        this.status = Status.PENDING;
        this.queuedAt = now;
    }

    public void markRunning() {
        this.status = Status.RUNNING;
    }

    public void markSucceeded(boolean hasFullSummary) {
        this.status = Status.SUCCEEDED;
        this.hasFullSummary = hasFullSummary;
        this.finishedAt = LocalDateTime.now();
        this.errorCode = null;
        this.errorMessage = null;
    }

    public void markFailed(String code, String message) {
        this.status = Status.FAILED;
        this.finishedAt = LocalDateTime.now();
        this.errorCode = code;
        this.errorMessage = message;
    }
}
