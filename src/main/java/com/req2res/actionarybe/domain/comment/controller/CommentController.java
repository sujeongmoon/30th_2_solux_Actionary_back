package com.req2res.actionarybe.domain.comment.controller;

import com.req2res.actionarybe.domain.comment.dto.CreateCommentRequestDTO;
import com.req2res.actionarybe.domain.comment.dto.CreateCommentResponseDTO;
import com.req2res.actionarybe.domain.comment.dto.GetCommentResponseDTO;
import com.req2res.actionarybe.domain.comment.service.CommentService;
import com.req2res.actionarybe.domain.post.dto.SortedResponseDTO;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/{post_id}/comments")
    public ResponseEntity<?> createComment(
            @RequestParam("post_id") Long postId,
            @RequestBody CreateCommentRequestDTO response,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long member_id=userDetails.getId();
        CreateCommentResponseDTO result = commentService.createComment(postId, response, member_id);
        return ResponseEntity.ok(Response.success("댓글 생성 성공", result));
    }

    // 최신순 정렬된 게시글 조회
    @GetMapping("/{post_id}/comments")
    public ResponseEntity<Response<GetCommentResponseDTO>> getLatestPosts(
            @RequestParam(defaultValue = "0", required = false) int page,
            @PathVariable("post_id") Long postId
    ) {
        Pageable pageable = PageRequest.of(
                page,
                10,
                Sort.by("createdAt")
        );

        GetCommentResponseDTO result = commentService.getCommentsByPostId(postId, pageable);
        return ResponseEntity.ok(Response.success("특정 게시글의 댓글 조회 성공", result));
    }
}
