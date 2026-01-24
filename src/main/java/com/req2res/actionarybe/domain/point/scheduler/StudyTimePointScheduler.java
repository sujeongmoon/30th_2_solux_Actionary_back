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

    // 매일 00:00 실행
    // 테스트용으로 1분마다 실행
    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    public void earnDailyStudyTimePoints() {

        log.info("[Scheduler] Daily study-time point job started");

        List<Member> members = memberRepository.findAll();
        log.info("[Scheduler] members size={}", members.size());

        for (Member member : members) {
            try {
                StudyTimePointResponseDTO dto = pointService.earnStudyTimePoint(member.getId());
                log.info("[Scheduler] earned userId={} point={} todaySeconds={}",
                        member.getId(), dto.getEarnedPoint(), dto.getTodayStudySeconds());

            } catch (CustomException e) {

                if (e.getErrorCode() == ErrorCode.STUDY_TIME_POINT_ALREADY_EARNED_TODAY
                        || e.getErrorCode() == ErrorCode.NOT_FOUND ) {

                    log.info("[Scheduler] skip userId={} code={} msg={}",
                            member.getId(), e.getErrorCode(), e.getMessage());
                    continue;
                }

                // 스킵 아닌 실패는 무조건 남기기
                log.error("[Scheduler] fail userId={} code={} msg={}",
                        member.getId(), e.getErrorCode(), e.getMessage(), e);

            } catch (Exception e) {
                log.error("[Scheduler] unexpected fail userId={}", member.getId(), e);
            }
        }

        log.info("[Scheduler] Daily study-time point job finished");
    }

}

