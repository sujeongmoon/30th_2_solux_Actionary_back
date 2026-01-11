package com.req2res.actionarybe.domain.studyTime.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.req2res.actionarybe.domain.studyTime.entity.StudyTimeManual;

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

	List<StudyTimeManual> findByUserIdAndManualDateBetween(Long userId, LocalDate start, LocalDate end);

	@Query("""
		    select coalesce(sum(sm.durationSecond), 0)
		    from StudyTimeManual sm
		    where sm.userId = :userId
		      and sm.manualDate between :startDate and :endDate
		""")
	long sumTodaySeconds(Long userId, LocalDate startDate, LocalDate endDate);
}
