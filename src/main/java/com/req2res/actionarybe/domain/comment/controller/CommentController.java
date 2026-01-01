package com.req2res.actionarybe.domain.comment.controller;

import com.req2res.actionarybe.domain.comment.dto.CreateCommentRequestDTO;
import com.req2res.actionarybe.domain.comment.dto.CreateCommentResponseDTO;
import com.req2res.actionarybe.domain.comment.service.CommentService;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{post_id}/comments")
    public ResponseEntity<?> createComment(
            @RequestParam("post_id") Long postId,
            @RequestBody CreateCommentRequestDTO response,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long member_id=userDetails.getId();
        CreateCommentResponseDTO result = commentService.createComment(postId, response, member_id);
        return ResponseEntity.ok(Response.success("댓글 생성 성공",result));
    }
}
