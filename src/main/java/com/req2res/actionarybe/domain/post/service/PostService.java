package com.req2res.actionarybe.domain.post.service;

import com.req2res.actionarybe.domain.post.dto.LatestResponseDTO;
import com.req2res.actionarybe.domain.post.dto.PageInfoDTO;
import com.req2res.actionarybe.domain.post.dto.PostSummaryDTO;
import com.req2res.actionarybe.domain.post.entity.Post;
import com.req2res.actionarybe.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public LatestResponseDTO getLatestPosts(Post.Type type, Pageable pageable) {

        Page<Post> page = (type == null)
                ? postRepository.findAll(pageable)
                : postRepository.findByType(type, pageable);

        List<PostSummaryDTO> posts = page.getContent().stream()
                .map(post -> new PostSummaryDTO(
                        post.getId(),
                        post.getType().name(),
                        post.getTitle(),
                        post.getMember().getNickname(),
                        post.getCommentsCount(),
                        post.getCreatedAt()
                ))
                .toList();

        PageInfoDTO pageInfo = new PageInfoDTO(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return new LatestResponseDTO(posts, pageInfo);
    }
}
