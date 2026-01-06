package com.req2res.actionarybe.domain.search.service;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.post.entity.Post;
import com.req2res.actionarybe.domain.search.dto.*;
import com.req2res.actionarybe.domain.search.repository.SearchRepository;
import com.req2res.actionarybe.domain.study.entity.Study;
import com.req2res.actionarybe.domain.study.repository.StudyParticipantRepository;
import com.req2res.actionarybe.domain.study.repository.StudyRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final StudyRepository studyRepository;
    private final StudyParticipantRepository studyParticipantRepository;
    private final SearchRepository searchRepository;

    // 1. 스터디 검색 API
    public StudySearchPageResponseDTO searchStudies(String q, String sortRaw, Integer pageRaw, Integer sizeRaw, Long loginUserId) {
        // q 필수 체크
        if (q == null || q.isBlank()) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "검색어(q)는 필수입니다.");
        }

        SearchSort sort = SearchSort.from(sortRaw);

        int page = (pageRaw == null ? 1 : pageRaw);
        int size = (sizeRaw == null ? 10 : sizeRaw);

        if (page < 1) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "page는 1 이상이어야 합니다.");
        }
        if (size < 1) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "size는 1 이상이어야 합니다.");
        }

        List<String> keywords = List.of(q.trim().split("\\s+")); // 공백 여러 개 포함 처리
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Study> resultPage = searchRepository.searchStudiesByKeywords(keywords, sort, pageable);


        if (resultPage.isEmpty()) {
            throw new CustomException(ErrorCode.SEARCH_NOT_FOUND);
        }

        List<StudySearchResponseDTO> content = resultPage.getContent().stream()
                .map(study -> {
                    boolean isJoined = false;
                    if (loginUserId != null) {
                        isJoined = studyParticipantRepository
                                .existsByStudy_IdAndMember_IdAndIsActiveTrue(study.getId(), loginUserId);
                    }

                    return StudySearchResponseDTO.builder()
                            .studyId(study.getId())
                            .title(study.getName())               // DB: name → 응답: title
                            .description(study.getDescription())
                            .category(String.valueOf(study.getCategory()))
                            .thumbnailUrl(study.getCoverImage())  // DB: cover_image → thumbnailUrl
                            .isJoined(isJoined)
                            .createdAt(study.getCreatedAt())
                            .build();
                })
                .toList();

        return StudySearchPageResponseDTO.builder()
                .content(content)
                .pageInfo(PageInfoDTO.builder()
                        .page(page) // 외부 1-based 그대로
                        .size(size)
                        .totalElements(resultPage.getTotalElements())
                        .totalPages(resultPage.getTotalPages())
                        .build())
                .build();
    }

    //2. 게시글 검색 API
    public PostSearchPageResponseDTO searchPosts(String q, String sortRaw, Integer pageRaw, Integer sizeRaw, Long loginUserId) {

        // q 필수 체크
        if (q == null || q.isBlank()) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "검색어(q)는 필수입니다.");
        }

        // sort는 RECENT / POPULAR
        SearchSort sort = SearchSort.from(sortRaw);

        int page = (pageRaw == null ? 1 : pageRaw);
        int size = (sizeRaw == null ? 10 : sizeRaw);

        if (page < 1) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "page는 1 이상이어야 합니다.");
        }
        if (size < 1) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "size는 1 이상이어야 합니다.");
        }

        // 검색어를 공백 기준으로 분리
        List<String> keywords = List.of(q.trim().split("\\s+")); // "알고리즘   인증" → ["알고리즘","인증"]

        Pageable pageable = PageRequest.of(page - 1, size); // 내부 0-based

        Page<Post> resultPage =
                searchRepository.searchPostsByKeywords(keywords, sort, pageable);


        if (resultPage.isEmpty()) {
            throw new CustomException(ErrorCode.SEARCH_NOT_FOUND);
        }

        List<PostSearchResponseDTO> content = resultPage.getContent().stream()
                .map(post -> {
                    boolean isMine = false;
                    if (loginUserId != null) {
                        isMine = post.getMember().getId().equals(loginUserId);
                    }

                    return PostSearchResponseDTO.builder()
                            .postId(post.getId())
                            .type(post.getType().name())
                            .title(post.getTitle())
                            .authorNickname(post.getMember().getNickname())
                            .createdAt(post.getCreatedAt())
                            .commentCount(post.getCommentsCount())
                            .isMine(isMine)
                            .build();
                })
                .toList();

        return PostSearchPageResponseDTO.builder()
                .content(content)
                .pageInfo(PageInfoDTO.builder()
                        .page(page) // 외부 1-based 그대로
                        .size(size)
                        .totalElements(resultPage.getTotalElements())
                        .totalPages(resultPage.getTotalPages())
                        .build())
                .build();
    }
}
