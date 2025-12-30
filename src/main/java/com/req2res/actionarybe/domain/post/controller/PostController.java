package com.req2res.actionarybe.domain.post.controller;

import com.req2res.actionarybe.domain.post.dto.LatestResponseDTO;
import com.req2res.actionarybe.domain.post.entity.Post;
import com.req2res.actionarybe.domain.post.service.PostService;
import com.req2res.actionarybe.global.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/posts")
public class PostController {
    private final PostService postService;

    @GetMapping("/latest")
    public ResponseEntity<Response<LatestResponseDTO>> getLatestPosts(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(required = false) Post.Type type
    ) {
        Pageable pageable = PageRequest.of(
                page,
                10,
                Sort.by("createdAt").descending()
        );

        LatestResponseDTO result = postService.getLatestPosts(type, pageable);

        return ResponseEntity.ok(
                Response.success("최신순 게시글 조회를 성공했습니다.", result)
        );
    }

}
