package com.req2res.actionarybe.domain.studyTime.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.req2res.actionarybe.domain.studyTime.entity.StudyTime;
import com.req2res.actionarybe.domain.studyTime.entity.Type;

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

	@Query("""
		select coalesce(sum(st.durationSecond), 0)
		from StudyTime st
		where st.studyParticipant.id = :studyParticipantId
				and st.type = :type
		""")
	long sumTotalDurationSeconds(long studyParticipantId, Type type);

}
