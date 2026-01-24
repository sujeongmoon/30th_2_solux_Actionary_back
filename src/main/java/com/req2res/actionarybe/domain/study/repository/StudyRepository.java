package com.req2res.actionarybe.domain.study.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.study.dto.StudyInteractionSummaryDto;
import com.req2res.actionarybe.domain.study.entity.Category;
import com.req2res.actionarybe.domain.study.entity.Study;
import org.springframework.data.repository.query.Param;

public interface StudyRepository extends JpaRepository<Study, Long> {

	Page<Study> findByIsPublic(boolean isPublic, Pageable pageable);

	Page<Study> findByIsPublicAndCategory(Boolean isPublic, Category category, Pageable pageable);

	@Query("""
		select new com.req2res.actionarybe.domain.study.dto.StudyInteractionSummaryDto(
		    s.id,
		    s.name,
		    s.coverImage,
		    s.description,
		    count(sp)
		)
		from Study s
		left join StudyParticipant sp
		  on sp.study = s
		 and sp.isActive = true
		group by s.id, s.name, s.coverImage, s.description
		order by count(sp) desc
		""")
	Page<StudyInteractionSummaryDto> findHitStudies(Pageable pageable);

	@Query("""
		    select new com.req2res.actionarybe.domain.study.dto.StudyInteractionSummaryDto(
		        s.id,
		        s.name,
		        s.coverImage,
		        s.description,
		        (select count(sp) from StudyParticipant sp where sp.study = s and sp.isActive = true)
		    )
		    from Study s
		    left join StudyParticipant mySp on mySp.study = s and mySp.member = :member
		    left join StudyLike myLike on myLike.study = s and myLike.member = :member
		    where s.creator = :member
		       or mySp.id is not null
		       or myLike.id is not null
		    group by s.id, s.name, s.coverImage, s.description
		    order by max(mySp.updatedAt) desc
		""")
	Page<StudyInteractionSummaryDto> findMyAllStudies(Member member, Pageable pageable);

	@Query("""
		    select new com.req2res.actionarybe.domain.study.dto.StudyInteractionSummaryDto(
		        s.id,
		        s.name,
		        s.coverImage,
		        s.description,
		        (select count(sp) from StudyParticipant sp where sp.study = s and sp.isActive = true)
		    )
		    from Study s
		    left join StudyParticipant mySp on mySp.study = s and mySp.member = :member
		    where s.creator = :member
		    group by s.id, s.name, s.coverImage, s.description
		    order by s.createdAt desc
		""")
	Page<StudyInteractionSummaryDto> findMyOwnedStudies(Member member, Pageable pageable);

	@Query("""
		    select new com.req2res.actionarybe.domain.study.dto.StudyInteractionSummaryDto(
		        s.id,
		        s.name,
		        s.coverImage,
		        s.description,
		        (select count(sp) from StudyParticipant sp where sp.study = s and sp.isActive = true)
		    )
		    from Study s
		    join StudyParticipant mySp on mySp.study = s and mySp.member = :member
		    group by s.id, s.name, s.coverImage, s.description
		    order by max(mySp.updatedAt) desc
		""")
	Page<StudyInteractionSummaryDto> findMyJoinedStudies(Member member, Pageable pageable);

	@Query("""
		    select new com.req2res.actionarybe.domain.study.dto.StudyInteractionSummaryDto(
		        s.id,
		        s.name,
		        s.coverImage,
		        s.description,
		        (select count(sp) from StudyParticipant sp where sp.study = s and sp.isActive = true)
		    )
		    from Study s
		    join StudyLike sl on sl.study = s
		    where sl.member = :member
		    group by s.id, s.name, s.coverImage, s.description, sl.createdAt
		    order by sl.createdAt desc
		""")
	Page<StudyInteractionSummaryDto> findMyLikedStudies(Member member, Pageable pageable);

	// RECENT: 최신순
	@Query(
			value = """
            SELECT s.*
            FROM study s
            WHERE (LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(s.description) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(s.category) LIKE LOWER(CONCAT('%', :q, '%')))
            ORDER BY s.created_at DESC
        """,
			countQuery = """
            SELECT COUNT(*)
            FROM study s
            WHERE (LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(s.description) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(s.category) LIKE LOWER(CONCAT('%', :q, '%')))
        """,
			nativeQuery = true
	)
	Page<Study> searchRecent(@Param("q") String q, Pageable pageable);

	// POPULAR: 즐겨찾기 수 기준 인기순
	@Query(
			value = """
            SELECT s.*
            FROM study s
            LEFT JOIN study_like sl ON sl.study_id = s.id
            WHERE (LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(s.description) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(s.category) LIKE LOWER(CONCAT('%', :q, '%')))
            GROUP BY s.id
            ORDER BY COUNT(sl.id) DESC, s.created_at DESC
        """,
			countQuery = """
            SELECT COUNT(*)
            FROM study s
            WHERE (LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(s.description) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(s.category) LIKE LOWER(CONCAT('%', :q, '%')))
        """,
			nativeQuery = true
	)
	Page<Study> searchPopular(@Param("q") String q, Pageable pageable);
}
