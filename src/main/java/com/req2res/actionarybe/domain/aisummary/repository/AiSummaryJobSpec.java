package com.req2res.actionarybe.domain.aisummary.repository;

import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryEnums;
import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryJob;
import org.springframework.data.jpa.domain.Specification;

public class AiSummaryJobSpec {

    public static Specification<AiSummaryJob> userIdEq(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("userId"), userId);
    }

    public static Specification<AiSummaryJob> statusEq(AiSummaryEnums.Status status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<AiSummaryJob> sourceTypeEq(AiSummaryEnums.SourceType sourceType) {
        return (root, query, cb) -> cb.equal(root.get("sourceType"), sourceType);
    }
}
