package com.req2res.actionarybe.domain.point.controller.scheduler;

import com.req2res.actionarybe.domain.point.scheduler.StudyTimePointScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/debug")
public class SchedulerDebugController {

    private final StudyTimePointScheduler scheduler;

    @PostMapping("/run-study-time")
    public ResponseEntity<String> run() {
        scheduler.runDailyJob();
        return ResponseEntity.ok("OK");
    }
}

