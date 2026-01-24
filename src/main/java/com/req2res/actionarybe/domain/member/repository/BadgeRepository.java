package com.req2res.actionarybe.domain.member.repository;

import com.req2res.actionarybe.domain.member.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {

    // 현재보다 requiredPoint가 큰 것 중 가장 작은 것(= 다음 기준점 뱃지)
    Optional<Badge> findTopByRequiredPointGreaterThanOrderByRequiredPointAsc(Long requiredPoint);
}
