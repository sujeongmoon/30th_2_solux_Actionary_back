package com.req2res.actionarybe.domain.study.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.req2res.actionarybe.domain.study.dto.HitStudySummaryDto;
import com.req2res.actionarybe.domain.study.entity.Category;
import com.req2res.actionarybe.domain.study.entity.Study;

public interface StudyRepository extends JpaRepository<Study, Long> {

	Page<Study> findByIsPublic(boolean isPublic, Pageable pageable);

	Page<Study> findByIsPublicAndCategory(Boolean isPublic, Category category, Pageable pageable);

	@Query("""
		select new com.req2res.actionarybe.domain.study.dto.HitStudySummaryDto(
		    s.id,
		    s.name,
		    s.coverImage,
		    s.description,
		    count(sp)
		)
		from Study s
		left join StudyParticipant sp
		  on sp.study = s
		 and sp.updatedAt is null
		group by s.id, s.name, s.coverImage, s.description
		order by count(sp) desc
		""")
	Page<HitStudySummaryDto> findHitStudies(Pageable pageable);
}
