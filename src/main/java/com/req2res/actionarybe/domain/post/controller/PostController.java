package com.req2res.actionarybe.domain.post.controller;

import com.req2res.actionarybe.domain.post.dto.*;
import com.req2res.actionarybe.domain.post.entity.Post;
import com.req2res.actionarybe.domain.post.service.PostService;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/posts")
public class PostController {
    private final PostService postService;

    // 게시글 생성
    @PostMapping("")
    public Response<CreatePostResponseDTO> createPost(
            @Valid @RequestBody CreatePostRequestDTO createPostRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long member_id = userDetails.getId();
        CreatePostResponseDTO result = postService.createPost(createPostRequestDTO, member_id);
        return Response.success("게시글 생성 성공",result);
    }

    // 게시글 조회 (post_id에 따른)
    @GetMapping("/{post_id}")
    public Response<GetPostResponseDTO> getPostById(
            @PathVariable("post_id") Long postId
    ){
        GetPostResponseDTO result = postService.getPost(postId);
        return Response.success("post_id에 따른 게시글 조회 성공", result);
    }

    // 게시글 수정
    @PatchMapping("/{post_id}")
    public Response<UpdatePostResponseDTO> updatePost(
            @PathVariable("post_id") Long postId,
            @Valid@RequestBody UpdatePostRequestDTO request
    ){
        UpdatePostResponseDTO result = postService.updatePost(postId, request);
        return Response.success("게시글 수정 성공", result);
    }

    // 게시글 삭제
    @DeleteMapping("/{post_id}")
    public Response<DeletePostResponseDTO> deletePost(
            @PathVariable("post_id") Long postId
    ){
        DeletePostResponseDTO result = postService.deletePost(postId);
        return Response.success("게시글 삭제 성공", result);
    }

    // 최신순 정렬된 게시글 조회
    @GetMapping("/latest")
    public Response<SortedResponseDTO> getLatestPosts(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(required = false) Post.Type type
    ) {
        Pageable pageable = PageRequest.of(
                page,
                10,
                Sort.by("createdAt").descending()
        );

        SortedResponseDTO result = postService.getSortedPosts(type, pageable);

        return Response.success("최신순 게시글 조회를 성공했습니다.", result);
    }

    // 인기순(댓글 개수) 정렬된 게시글 조회
    @GetMapping("/popular")
    public Response<SortedResponseDTO> getPopularPosts(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(required = false) Post.Type type
    ) {
        Pageable pageable = PageRequest.of(
                page,
                10,
                Sort.by("commentsCount").descending()
        );

        SortedResponseDTO result = postService.getSortedPosts(type, pageable);

        return Response.success("인기순 게시글 조회를 성공했습니다.", result);
    }
}
