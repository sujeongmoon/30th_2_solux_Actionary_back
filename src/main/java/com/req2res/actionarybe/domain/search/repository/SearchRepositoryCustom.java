package com.req2res.actionarybe.domain.search.repository;

import com.req2res.actionarybe.domain.post.entity.Post;
import com.req2res.actionarybe.domain.search.dto.SearchSort;
import com.req2res.actionarybe.domain.study.entity.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchRepositoryCustom {
    Page<Study> searchStudiesByKeywords(List<String> keywords, SearchSort sort, Pageable pageable);

    Page<Post> searchPostsByKeywords(List<String> keywords, SearchSort sort, Pageable pageable);
}
