package com.req2res.actionarybe.domain.study.scheduler;

import com.req2res.actionarybe.domain.notification.service.NotificationService;
import com.req2res.actionarybe.domain.study.service.StudyService;
import com.req2res.actionarybe.domain.studyTime.entity.Type;
import com.req2res.actionarybe.domain.studyTime.repository.StudyTimeManualRepository;
import com.req2res.actionarybe.domain.studyTime.repository.StudyTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DailyStudySummaryScheduler {

    private final StudyService studyService; // 공부량 집계 로직 있는 서비스
    private final NotificationService notificationService;
    private final StudyTimeRepository studyTimeRepository;
    private final StudyTimeManualRepository studyTimeManualRepository;

    @Scheduled(cron = "0 */1 * * * *", zone = "Asia/Seoul")
    @Transactional
    public void sendDailyStudySummary() {
        System.out.println("[Scheduler] fired");

        List<Long> userIds = studyService.findUsersStudiedToday();
        System.out.println("[Scheduler] userIds=" + userIds);

        for (Long userId : userIds) {
            String summaryText = studyService.buildYesterdaySummaryText(userId);
            System.out.println("[Scheduler] notify userId=" + userId + " text=" + summaryText);
            notificationService.notifyDailyStudySummary(userId, summaryText);
        }
    }




}
