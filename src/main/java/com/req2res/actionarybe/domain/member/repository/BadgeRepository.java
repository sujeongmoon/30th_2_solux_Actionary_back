package com.req2res.actionarybe.domain.member.repository;

import com.req2res.actionarybe.domain.member.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
