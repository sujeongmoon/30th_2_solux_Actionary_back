package com.req2res.actionarybe.domain.studyTime.repository;

import com.req2res.actionarybe.domain.point.entity.StudyTimeManual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StudyTimeManualRepository extends JpaRepository<StudyTimeManual, Long> {

    List<StudyTimeManual> findAllByUserIdAndManualDate(Long userId, LocalDate manualDate);

    @Query("""
        select coalesce(sum(stm.durationSecond), 0)
        from StudyTimeManual stm
        where stm.userId = :userId
          and stm.manualDate = :manualDate
    """)
    long sumDurationSecondByUserIdAndManualDate(@Param("userId") Long userId,
                                                @Param("manualDate") LocalDate manualDate);
}
