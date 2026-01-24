package com.req2res.actionarybe.domain.studyTime.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.req2res.actionarybe.domain.member.entity.Member;
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

	@Query("""
		SELECT st
		FROM StudyTime st
		JOIN st.studyParticipant sp
		WHERE sp.member = :member
		  AND st.type = :type
		  AND st.createdAt BETWEEN :start AND :end
		""")
	List<StudyTime> findStudyTimeByMember(Member member, Type type, LocalDateTime start, LocalDateTime end);

	// 오늘 공부한 유저 목록 (자동 기록)
	@Query("""
    select distinct st.studyParticipant.member.id
    from StudyTime st
    where st.createdAt between :start and :end
      and st.type = :studyType
""")
	List<Long> findDistinctUserIdsStudiedToday(
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end,
			@Param("studyType") Type studyType
	);


	// 유저별 오늘 공부시간 합계(초) (자동 기록)
	@Query("""
    select coalesce(sum(st.durationSecond), 0)
    from StudyTime st
    where st.studyParticipant.member.id = :userId
      and st.createdAt between :start and :end
      and st.type = :studyType
""")
	int sumStudySecondsTodayByUserId(
			@Param("userId") Long userId,
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end,
			@Param("studyType") Type studyType
	);

}
