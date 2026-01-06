package com.req2res.actionarybe.domain.search.repository;

import com.req2res.actionarybe.domain.post.entity.Post;
import com.req2res.actionarybe.domain.search.dto.SearchSort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchRepository extends JpaRepository<Post, Long>, SearchRepositoryCustom {

    // 최신순 (RECENT)
    @EntityGraph(attributePaths = {"member"})
    @Query("""
        SELECT p
        FROM Post p
        WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(p.text)  LIKE LOWER(CONCAT('%', :q, '%'))
        ORDER BY p.createdAt DESC
    """)
    Page<Post> searchPostRecent(@Param("q") String q, Pageable pageable);

    // 인기순 (POPULAR) : 댓글 수 기준, 동률이면 최신순
    @EntityGraph(attributePaths = {"member"})
    @Query("""
        SELECT p
        FROM Post p
        WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(p.text)  LIKE LOWER(CONCAT('%', :q, '%'))
        ORDER BY p.commentsCount DESC, p.createdAt DESC
    """)
    Page<Post> searchPostPopular(@Param("q") String q, Pageable pageable);

    // 띄어쓰기 포함 여러 단어 검색
    Page<Post> searchPostsByKeywords(List<String> keywords, SearchSort sort, Pageable pageable);
}