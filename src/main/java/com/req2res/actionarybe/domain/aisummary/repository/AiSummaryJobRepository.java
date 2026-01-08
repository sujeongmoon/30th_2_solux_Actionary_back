package com.req2res.actionarybe.domain.aisummary.repository;

import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiSummaryJobRepository extends JpaRepository<AiSummaryJob, Long> {
    Optional<AiSummaryJob> findByJobId(String jobId);
    Page<AiSummaryJob> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);
}
