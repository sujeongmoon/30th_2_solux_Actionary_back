package com.req2res.actionarybe.domain.post.controller;

import com.req2res.actionarybe.domain.post.dto.CreatePostRequestDTO;
import com.req2res.actionarybe.domain.post.dto.CreatePostResponseDTO;
import com.req2res.actionarybe.domain.post.dto.SortedResponseDTO;
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

    @PostMapping("/{postId}")
    public ResponseEntity<Response<CreatePostResponseDTO>> createPost(
            @Valid @RequestBody CreatePostRequestDTO createPostRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long member_id = userDetails.getId();
        CreatePostResponseDTO result = postService.createPost(createPostRequestDTO, member_id);
        return ResponseEntity.ok(Response.success("게시글 생성 성공",result));
    }

    @GetMapping("/latest")
    public ResponseEntity<Response<SortedResponseDTO>> getLatestPosts(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(required = false) Post.Type type
    ) {
        Pageable pageable = PageRequest.of(
                page,
                10,
                Sort.by("createdAt").descending()
        );

        SortedResponseDTO result = postService.getSortedPosts(type, pageable);

        return ResponseEntity.ok(
                Response.success("최신순 게시글 조회를 성공했습니다.", result)
        );
    }

    @GetMapping("/popular")
    public ResponseEntity<Response<SortedResponseDTO>> getPopularPosts(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(required = false) Post.Type type
    ) {
        Pageable pageable = PageRequest.of(
                page,
                10,
                Sort.by("commentsCount").descending()
        );

        SortedResponseDTO result = postService.getSortedPosts(type, pageable);

        return ResponseEntity.ok(
                Response.success("인기순 게시글 조회를 성공했습니다.", result)
        );
    }
}
