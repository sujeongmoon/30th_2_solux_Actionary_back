package com.req2res.actionarybe.domain.study.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.req2res.actionarybe.domain.study.dto.RankingDurationDto;
import com.req2res.actionarybe.domain.study.dto.StudyParticipantUserDto;
import com.req2res.actionarybe.domain.study.entity.Study;
import com.req2res.actionarybe.domain.study.entity.StudyParticipant;

public interface StudyParticipantRepository extends JpaRepository<StudyParticipant, Long> {

	int countByStudyAndIsActiveTrue(Study study);

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

	@Query("""
			select new com.req2res.actionarybe.domain.study.dto.StudyParticipantUserDto(
					sp.id,
					m.id,
					m.nickname,
					m.profileImageUrl,
					m.badge.id,
					m.badge.imageUrl
					)
					from StudyParticipant sp
					join sp.member m
					where sp.study.id = :studyId
				 	  and m.id = :memberId
				 	  and sp.isActive = true
		""")
	Optional<StudyParticipantUserDto> findParticipantUserByStudyAndMemberAndIsActiveTrue(Long studyId, Long memberId);

	@Query("""
		select new com.req2res.actionarybe.domain.study.dto.StudyParticipantUserDto(
							sp.id,
							m.id,
							m.nickname,
							m.profileImageUrl,
							m.badge.id,
							m.badge.imageUrl
							)
							from StudyParticipant sp
							join sp.member m
							where sp.study.id = :studyId
						 	  and sp.isActive = true
		
		""")
	List<StudyParticipantUserDto> findParticipantUserByStudyAndIsActiveTrue(Long studyId);
}
