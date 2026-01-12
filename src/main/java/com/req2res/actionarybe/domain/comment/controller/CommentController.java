package com.req2res.actionarybe.domain.comment.controller;

import com.req2res.actionarybe.domain.comment.dto.*;
import com.req2res.actionarybe.domain.comment.service.CommentService;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ===================== 댓글 생성 (POST) =====================
    @Operation(summary = "댓글 생성", description = "특정 게시글에 댓글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "댓글 생성 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "댓글이 성공적으로 등록되었습니다.",
                      "data": {
                        "commentId": 1,
                        "content": "좋은 질문입니다.",
                        "isSecret": false,
                        "createdAt": "2023-10-27T11:00:00",
                        "author": {
                          "memberId": 2,
                          "nickname": "개발자B"
                        }
                      }
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 입력값이 빠지거나 형식이 틀린 경우",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "요청 값이 올바르지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인 필요 - 토큰이 없거나 유효하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "로그인이 필요합니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 댓글 작성 권한이 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "댓글 작성 권한이 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "서버 오류가 발생했습니다."
                    }
                    """))
            )
    })
    @PostMapping("/{postId}/comments")
    public Response<CommentResponseDTO> createComment(
            @PathVariable("postId") Long postId,
            @RequestBody CreateCommentRequestDTO response,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getId();
        CommentResponseDTO result = commentService.createComment(postId, response, memberId);
        return Response.success("댓글 생성 성공", result);
    }

    // ===================== 댓글 조회 (GET - 최신순) =====================
    @Operation(summary = "댓글 조회", description = "특정 게시글의 댓글을 최신순으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "댓글 조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "댓글이 성공적으로 조회되었습니다.",
                      "data": {
                        "comments": [
                          {
                            "commentId": 1,
                            "content": "좋은 질문입니다.",
                            "createdAt": "2023-10-27T11:00:00",
                            "isSecret": false,
                            "author": {
                              "memberId": 2,
                              "nickname": "개발자B",
                              "profileImageUrl": "https://.../userB.png",
                              "badgeId": 1
                            }
                          },
                          {
                            "commentId": 2,
                            "content": "저도 궁금했어요!",
                            "createdAt": "2023-10-27T12:00:00",
                            "isSecret": false,
                            "author": {
                              "memberId": 3,
                              "nickname": "개발자C",
                              "profileImageUrl": "https://.../userC.png",
                              "badgeId": 2
                            }
                          }
                        ],
                        "pageInfo": {
                          "page": 0,
                          "size": 10,
                          "totalElements": 25,
                          "totalPages": 3
                        }
                      }
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 페이지 파라미터 오류",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "요청 형식이 올바르지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "찾을 수 없음 - 게시글 또는 댓글이 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "댓글을 찾을 수 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "서버 오류가 발생했습니다."
                    }
                    """))
            )
    })
    @GetMapping("/{postId}/comments")
    public Response<GetCommentResponseDTO> getLatestPosts(
            @RequestParam(defaultValue = "0", required = false) int page,
            @PathVariable("postId") Long postId
    ) {
        Pageable pageable = PageRequest.of(
                page,
                10,
                Sort.by("createdAt")
        );

        GetCommentResponseDTO result = commentService.getCommentsByPostId(postId, pageable);
        return Response.success("특정 게시글의 댓글 조회 성공", result);
    }

    // ===================== 댓글 수정 (PATCH) =====================
    @Operation(summary = "댓글 수정", description = "댓글 내용을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "댓글 수정 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "게시글 댓글 수정 성공",
                      "data": {
                        "commentId": 1,
                        "content": "수정된 댓글 내용입니다.",
                        "isSecret": true,
                        "createdAt": "2023-10-27T11:00:00",
                        "author": {
                          "memberId": 2,
                          "nickname": "개발자B"
                        }
                      }
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 수정할 내용이 올바르지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "요청 값이 올바르지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인 필요",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "로그인이 필요합니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "수정 권한 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "수정 권한이 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "찾을 수 없음 - 댓글이 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "해당 댓글을 찾을 수 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "서버 오류가 발생했습니다."
                    }
                    """))
            )
    })
    @PatchMapping("/comments/{commentId}")
    public Response<CommentResponseDTO> updateComment(
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody UpdateCommentRequestDTO request
    ){
        CommentResponseDTO result = commentService.updateComment(commentId, request);
        return Response.success("게시글 댓글 수정 성공", result);
    }

    // ===================== 댓글 삭제 (DELETE) =====================
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "댓글 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "게시글 댓글 삭제 성공",
                      "data": {
                        "commentId": 2
                      }
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 삭제할 ID가 잘못된 경우",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "요청 정보가 올바르지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인 필요",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "로그인이 필요합니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "삭제 권한 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "삭제 권한이 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "찾을 수 없음 - 댓글이 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "해당 댓글을 찾을 수 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "서버 오류가 발생했습니다."
                    }
                    """))
            )
    })
    @DeleteMapping("/comments/{commentId}")
    public Response<DeleteCommentResponseDTO> deleteComment(
            @PathVariable("commentId") Long commentId
    ){
        DeleteCommentResponseDTO result = commentService.deleteComment(commentId);
        return Response.success("게시글 댓글 삭제 성공", result);
    }
}