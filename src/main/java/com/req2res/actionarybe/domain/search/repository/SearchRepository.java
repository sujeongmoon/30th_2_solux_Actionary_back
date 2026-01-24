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

    // 띄어쓰기 포함 여러 단어 검색
    Page<Post> searchPostsByKeywords(List<String> keywords, SearchSort sort, Pageable pageable);
}