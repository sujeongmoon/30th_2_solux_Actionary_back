package com.req2res.actionarybe.domain.study.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.req2res.actionarybe.domain.study.dto.RankingDurationDto;
import com.req2res.actionarybe.domain.study.entity.Study;
import com.req2res.actionarybe.domain.study.entity.StudyParticipant;

public interface StudyParticipantRepository extends JpaRepository<StudyParticipant, Long> {

	int countByStudyAndUpdatedAtIsNull(Study study);

	@Query("""
		    select new com.req2res.actionarybe.domain.study.dto.RankingDurationDto(
		        m.id,
		        m.nickname,
		        sum(st.durationSecond)
		    )
		    from StudyParticipant sp
		    join sp.member m
		    join StudyTime st on st.studyParticipant.id = sp.id
		    where sp.study.id = :studyId
		      and st.type = 'STUDY'
		      and st.createdAt >= :startOfDay
		    group by m.id, m.nickname
		""")
	List<RankingDurationDto> findTodayDurations(
		Long studyId,
		LocalDateTime startOfDay
	);

	@Query("""
		    select new com.req2res.actionarybe.domain.study.dto.RankingDurationDto(
		        m.id,
		        m.nickname,
		        sum(st.durationSecond)
		    )
		    from StudyParticipant sp
		    join sp.member m
		    join StudyTime st on st.studyParticipant.id = sp.id
		    where sp.study.id = :studyId
		      and st.type = 'STUDY'
		    group by m.id, m.nickname
		""")
	List<RankingDurationDto> findTotalDurations(Long studyId);
}
