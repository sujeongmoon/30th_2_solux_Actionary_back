package com.req2res.actionarybe.domain.point.scheduler;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.point.dto.StudyTimePointResponseDTO;
import com.req2res.actionarybe.domain.point.service.PointService;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudyTimePointScheduler {

    private final PointService pointService;
    private final MemberRepository memberRepository;

    // 매일 00:01 (어제 공부시간 기준 정산)
    @Scheduled(cron = "0 1 0 * * *", zone = "Asia/Seoul")
    public void earnDailyStudyTimePoints() {

        log.info("[Scheduler] Daily study-time point job started (yesterday summary)");

        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            try {
                pointService.earnStudyTimePoint(member.getId());
            } catch (CustomException e) {

                // 정상 스킵 케이스
                if (e.getErrorCode() == ErrorCode.STUDY_TIME_POINT_ALREADY_EARNED_TODAY ||
                        e.getMessage().contains("공부시간")) {

                    log.info(
                            "[Scheduler] skip userId={} code={} msg={}",
                            member.getId(),
                            e.getErrorCode(),
                            e.getMessage()
                    );
                    continue;
                }

                // 진짜 에러는 로그로 남김
                log.error(
                        "[Scheduler] fail userId={} code={} msg={}",
                        member.getId(),
                        e.getErrorCode(),
                        e.getMessage(),
                        e
                );
            }
        }

        log.info("[Scheduler] Daily study-time point job finished");
    }
}
