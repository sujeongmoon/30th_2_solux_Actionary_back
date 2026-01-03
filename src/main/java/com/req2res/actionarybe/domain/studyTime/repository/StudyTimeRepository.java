package com.req2res.actionarybe.domain.studyTime.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.req2res.actionarybe.domain.studyTime.entity.StudyTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface StudyTimeRepository extends JpaRepository<StudyTime, Long> {
    @Query("""
        select coalesce(sum(st.durationSecond), 0)
        from StudyTime st
        join st.studyParticipant sp
        join sp.member m
        where m.id = :memberId
          and st.createdAt >= :start
          and st.createdAt < :end
          and st.type = 'STUDY'
    """)
    long sumTodaySeconds(@Param("memberId") Long memberId,
                         @Param("start") LocalDateTime start,
                         @Param("end") LocalDateTime end);
}
