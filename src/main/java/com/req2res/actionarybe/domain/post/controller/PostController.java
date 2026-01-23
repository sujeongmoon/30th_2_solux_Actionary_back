package com.req2res.actionarybe.domain.post.controller;

import com.req2res.actionarybe.domain.post.dto.*;
import com.req2res.actionarybe.domain.post.entity.Post;
import com.req2res.actionarybe.domain.post.service.PostService;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/posts")
public class PostController {

    private final PostService postService;

    // ===================== 게시글 생성 (POST) =====================
    @Operation(summary = "게시글 생성", description = "게시글을 새로 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "게시글 생성 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "게시글이 성공적으로 생성됨.",
                      "data": {
                        "postId": 108,
                        "title": "오늘의 스터디",
                        "nickname": "닉네임1234",
                        "createdAt": "2025-01-11T08:10:20"
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
                    description = "로그인 필요 - Authorization 헤더가 없거나 토큰이 유효하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "로그인이 필요합니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 게시글 생성 권한이 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "게시글 생성 권한이 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복됨 - 이미 존재하는 게시글",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "이미 존재하는 게시글입니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 서버 내부 문제",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "서버 오류가 발생했습니다."
                    }
                    """))
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<CreatePostResponseDTO> createPost(
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart("post") CreatePostRequestDTO createPostRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long member_id = userDetails.getId();
        CreatePostResponseDTO result = postService.createPost(createPostRequestDTO, member_id, images);
        return Response.success("게시글 생성 성공",result);
    }

    // ===================== 게시글 상세 조회 (GET) =====================
    @Operation(summary = "게시글 상세 조회", description = "post_id로 게시글 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "게시글 상세 조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "게시글 상세 조회에 성공했습니다.",
                      "data": {
                        "post": {
                          "postId": 101,
                          "type": "질문",
                          "title": "ERD 설계 질문입니다",
                          "textContent": "게시글 본문 내용... 텍스트입니다.",
                          "commentCount": 2,
                          "createdAt": "2023-10-27T10:00:00"
                        },
                        "postImageUrls": [
                          "https://storage.com/aaa.jpg",
                          "https://storage.com/bbb.jpg"
                        ],
                        "author": {
                          "memberId": 1,
                          "nickname": "개발자A",
                          "profileImageUrl": "https://.../default.png",
                          "badge": 0
                        }
                      }
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - ID 형식이 잘못된 경우",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "요청 형식이 올바르지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "찾을 수 없음 - 게시글이 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "해당 게시글을 찾을 수 없습니다."
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
    @GetMapping("/{post_id}")
    public Response<GetPostResponseDTO> getPostById(
            @PathVariable("post_id") Long postId
    ){
        GetPostResponseDTO result = postService.getPost(postId);
        return Response.success("post_id에 따른 게시글 조회 성공", result);
    }

    // ===================== 게시글 수정 (PATCH) =====================
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다. 수정된 필드만 보내도 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "게시글 수정 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "게시글 수정 성공"
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 수정할 내용이 올바르지 않을 때",
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
                    description = "수정 금지 - 수정 권한이 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "수정 권한이 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "찾을 수 없음 - 수정하려는 게시글이 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "해당 게시글을 찾을 수 없습니다."
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
    @PatchMapping("/{post_id}")
    public Response<UpdatePostResponseDTO> updatePost(
            @PathVariable("post_id") Long postId,
            @RequestPart(value = "addImages", required = false) List<MultipartFile> addImages,
            @RequestPart(value = "delImages", required = false) DeleteImagesRequestDTO delImages,
            @RequestPart(value = "post", required = false) UpdatePostRequestDTO posts
    ){
        UpdatePostResponseDTO result = postService.updatePost(postId, addImages, delImages, posts);
        return Response.success("게시글 수정 성공", result);
    }

    // ===================== 게시글 삭제 (DELETE) =====================
    @Operation(summary = "게시글 삭제", description = "post_id로 게시글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "게시글 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "게시글이 성공적으로 삭제되었습니다.",
                      "data": {
                        "postId": 108
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
                    description = "삭제 금지 - 삭제 권한이 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "삭제 권한이 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "찾을 수 없음 - 이미 삭제되었거나 존재하지 않는 게시글",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "해당 게시글을 찾을 수 없습니다."
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
    @DeleteMapping("/{post_id}")
    public Response<DeletePostResponseDTO> deletePost(
            @PathVariable("post_id") Long postId
    ){
        DeletePostResponseDTO result = postService.deletePost(postId);
        return Response.success("게시글 삭제 성공", result);
    }

    // ===================== 최신순 게시글 조회 (GET) =====================
    @Operation(summary = "최신순 게시글 조회", description = "게시글을 최신순으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "게시글 목록 조회에 성공했습니다.",
                      "data": {
                        "posts": [
                          {
                            "postId": 101,
                            "type": "소통",
                            "title": "ERD 설계 질문입니다",
                            "nickname": "개발자A",
                            "createdAt": "2023-10-27T10:00:00",
                            "commentCount": 2
                          }
                        ],
                        "pageInfo": {
                          "page": 0,
                          "size": 10,
                          "totalElements": 150,
                          "totalPages": 8
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

    // ===================== 인기순 게시글 조회 (GET) =====================
    @Operation(summary = "인기순 게시글 조회", description = "게시글을 댓글 개수 기준 인기순으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "게시글 목록 조회에 성공했습니다.",
                      "data": {
                        "posts": [
                          {
                            "postId": 100,
                            "type": "소통",
                            "title": "API 명세서 질문",
                            "nickname": "개발자B",
                            "createdAt": "2023-10-26T15:00:00",
                            "commentCount": 5
                          }
                        ],
                        "pageInfo": {
                          "page": 0,
                          "size": 10,
                          "totalElements": 150,
                          "totalPages": 8
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