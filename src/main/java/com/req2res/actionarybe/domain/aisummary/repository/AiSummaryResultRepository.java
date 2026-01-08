package com.req2res.actionarybe.domain.aisummary.repository;

import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiSummaryResultRepository
        extends JpaRepository<AiSummaryResult, Long> {

    Optional<AiSummaryResult> findByJobId(String jobId);
}
