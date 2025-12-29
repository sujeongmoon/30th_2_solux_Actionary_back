package com.req2res.actionarybe.domain.notification.repository;

import com.req2res.actionarybe.domain.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // limit 적용(최신순)
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

    // limit 없이 전체(최신순)
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
}

