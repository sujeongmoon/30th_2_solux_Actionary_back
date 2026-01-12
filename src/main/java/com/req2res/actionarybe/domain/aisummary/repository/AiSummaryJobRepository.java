package com.req2res.actionarybe.domain.aisummary.repository;

import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AiSummaryJobRepository
        extends JpaRepository<AiSummaryJob, Long>, JpaSpecificationExecutor<AiSummaryJob> {
    Optional<AiSummaryJob> findByJobId(String jobId);
}
