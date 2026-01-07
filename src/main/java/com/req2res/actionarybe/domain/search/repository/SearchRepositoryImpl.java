package com.req2res.actionarybe.domain.search.repository;

import com.req2res.actionarybe.domain.post.entity.Post;
import com.req2res.actionarybe.domain.search.dto.SearchSort;
import com.req2res.actionarybe.domain.study.entity.Study;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SearchRepositoryImpl implements SearchRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    // 1. 스터디 검색
    @Override
    public Page<Study> searchStudiesByKeywords(List<String> keywords, SearchSort sort, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // content query
        CriteriaQuery<Study> cq = cb.createQuery(Study.class);
        Root<Study> study = cq.from(Study.class);

        cq.select(study).distinct(true);
        cq.where(buildStudyOrPredicate(cb, study, keywords));

        // 정렬: POPULAR=즐겨찾기 수, RECENT=createdAt
        if (sort == SearchSort.POPULAR) {
            cq.orderBy(cb.desc(study.get("bookmarkCount")), cb.desc(study.get("createdAt")));
        } else {
            cq.orderBy(cb.desc(study.get("createdAt")));
        }

        TypedQuery<Study> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Study> content = query.getResultList();

        // count query
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Study> countRoot = countCq.from(Study.class);
        countCq.select(cb.countDistinct(countRoot));
        countCq.where(buildStudyOrPredicate(cb, countRoot, keywords));
        Long total = em.createQuery(countCq).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    private Predicate buildStudyOrPredicate(CriteriaBuilder cb, Root<Study> study, List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) return cb.disjunction();

        List<Predicate> ors = new ArrayList<>();
        for (String kw : keywords) {
            if (kw == null || kw.isBlank()) continue;
            String pattern = "%" + kw.toLowerCase() + "%";

            ors.add(cb.like(cb.lower(study.get("name")), pattern));
            ors.add(cb.like(cb.lower(study.get("description")), pattern));
            ors.add(cb.like(cb.lower(study.get("category").as(String.class)), pattern));
        }
        return ors.isEmpty() ? cb.disjunction() : cb.or(ors.toArray(new Predicate[0]));
    }
    // 2. 게시글 검색
    @Override
    public Page<Post> searchPostsByKeywords(List<String> keywords, SearchSort sort, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // ===== content query =====
        CriteriaQuery<Post> cq = cb.createQuery(Post.class);
        Root<Post> post = cq.from(Post.class);

        // member fetch join (N+1 방지)
        post.fetch("member", JoinType.LEFT);

        cq.select(post).distinct(true);

        Predicate where = buildOrKeywordPredicate(cb, post, keywords);
        cq.where(where);

        // 정렬
        if (sort == SearchSort.POPULAR) {
            cq.orderBy(cb.desc(post.get("commentsCount")), cb.desc(post.get("createdAt")));
        } else {
            cq.orderBy(cb.desc(post.get("createdAt")));
        }

        TypedQuery<Post> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Post> content = query.getResultList();

        // ===== count query =====
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Post> countRoot = countCq.from(Post.class);
        countCq.select(cb.countDistinct(countRoot));
        countCq.where(buildOrKeywordPredicate(cb, countRoot, keywords));

        Long total = em.createQuery(countCq).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 여러 단어 OR 검색:
     * (title like %kw1% OR text like %kw1% OR title like %kw2% OR text like %kw2% ...)
     */
    private Predicate buildOrKeywordPredicate(CriteriaBuilder cb, Root<Post> post, List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return cb.disjunction(); // false
        }

        List<Predicate> orPredicates = new ArrayList<>();

        for (String kw : keywords) {
            if (kw == null || kw.isBlank()) continue;

            String pattern = "%" + kw.toLowerCase() + "%";
            orPredicates.add(cb.like(cb.lower(post.get("title")), pattern));
            orPredicates.add(cb.like(cb.lower(post.get("text")), pattern));
        }

        if (orPredicates.isEmpty()) return cb.disjunction();

        return cb.or(orPredicates.toArray(new Predicate[0]));
    }
}
