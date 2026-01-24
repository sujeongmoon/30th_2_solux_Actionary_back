package com.req2res.actionarybe.domain.notification.repository;

import com.req2res.actionarybe.domain.notification.entity.Notification;
import com.req2res.actionarybe.domain.notification.entity.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // limit 적용(최신순)
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

    // limit 없이 전체(최신순)
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    // 각 알림이 정한 시간 범위안에 존재하는지 확인하는 메소드
    boolean existsByReceiverIdAndTypeAndCreatedAtBetween(
            Long receiverId,
            NotificationType type,
            LocalDateTime start,
            LocalDateTime end
    );
}

