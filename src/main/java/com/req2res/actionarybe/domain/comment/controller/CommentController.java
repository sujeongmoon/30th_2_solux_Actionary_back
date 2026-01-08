package com.req2res.actionarybe.domain.comment.controller;

import com.req2res.actionarybe.domain.comment.dto.*;
import com.req2res.actionarybe.domain.comment.service.CommentService;
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
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> createComment(
            @PathVariable("postId") Long postId,
            @RequestBody CreateCommentRequestDTO response,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId=userDetails.getId();
        CommentResponseDTO result = commentService.createComment(postId, response, memberId);
        return ResponseEntity.ok(Response.success("댓글 생성 성공", result));
    }

    // 최신순 정렬된 댓글 조회
    @GetMapping("/{postId}/comments")
    public ResponseEntity<Response<GetCommentResponseDTO>> getLatestPosts(
            @RequestParam(defaultValue = "0", required = false) int page,
            @PathVariable("postId") Long postId
    ) {
        Pageable pageable = PageRequest.of(
                page,
                10,
                Sort.by("createdAt")
        );

        GetCommentResponseDTO result = commentService.getCommentsByPostId(postId, pageable);
        return ResponseEntity.ok(Response.success("특정 게시글의 댓글 조회 성공", result));
    }

    // 댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Response<CommentResponseDTO>> updateComment(
        @PathVariable("commentId") Long commentId,
        @Valid@RequestBody UpdateCommentRequestDTO request
    ){
        CommentResponseDTO result = commentService.updateComment(commentId, request);
        return ResponseEntity.ok(Response.success("게시글 댓글 수정 성공", result));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Response<DeleteCommentResponseDTO>> deleteComment(
            @PathVariable("commentId") Long commentId
    ){
        DeleteCommentResponseDTO result=commentService.deleteComment(commentId);
        return ResponseEntity.ok(Response.success("게시글 댓글 삭제 성공", result));
    }
}
