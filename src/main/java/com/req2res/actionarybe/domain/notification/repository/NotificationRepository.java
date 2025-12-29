package com.req2res.actionarybe.domain.notification.repository;

import com.req2res.actionarybe.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
