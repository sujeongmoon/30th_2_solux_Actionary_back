package com.req2res.actionarybe.domain.point.repository;

import com.req2res.actionarybe.domain.point.entity.UserPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {
    java.util.Optional<UserPoint> findByMember_Id(Long memberId);
}
