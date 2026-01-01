package com.req2res.actionarybe.domain.post.service;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.post.dto.*;
import com.req2res.actionarybe.domain.post.entity.Post;
import com.req2res.actionarybe.domain.post.entity.PostImage;
import com.req2res.actionarybe.domain.post.repository.PostRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 게시글 생성
    @Transactional
    public CreatePostResponseDTO createPost(CreatePostRequestDTO request, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post.Type type;
        try {
            type = Post.Type.valueOf(request.getType());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        Post post = new Post(
                member,
                type,
                request.getTitle(),
                request.getContent().getTextContent()
        );

        int order = 0;
        for (String imageUrl : request.getContent().getImageUrls()) {
            post.addImage(new PostImage(post, imageUrl, order++));
        }

        postRepository.save(post);

        return new CreatePostResponseDTO(
                post.getId(),
                post.getTitle(),
                member.getNickname(),
                post.getCreatedAt()
        );
    }

    // 게시글 조회 (post_id에 따른)
    public GetPostResponseDTO getPost(Long post_id) {
        Post post=postRepository.findById(post_id)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));

        PostInfoDTO postInfo = new PostInfoDTO(
                post.getId(),
                post.getType().toString(),
                post.getTitle(),
                post.getText(),
                post.getCommentsCount(),
                post.getCreatedAt()
        );

        PostAuthorDTO postAuthor = new PostAuthorDTO(
                post.getMember().getId(),
                post.getMember().getNickname(),
                post.getMember().getProfileImageUrl(),
                post.getMember().getBadge().getId()
        );

        // 서버 꺼졌다가 켜져도, .getImages()하면 SQL 쿼리 날림 (@OneToMany(fetch = FetchType.LAZY이기에)
        List<String> images=post.getImages().stream()
                .map(postImage->postImage.getImageUrl()).toList();

        return new GetPostResponseDTO(
                postInfo,
                images,
                postAuthor
        );
    }

    // 게시글 수정
    @Transactional
    public UpdatePostResponseDTO updatePost(Long post_id, UpdatePostRequestDTO request) {
        Post post=postRepository.findById(post_id)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));

        if(request.getType()!=null)
            post.setType(Post.Type.valueOf(request.getType())); // request의 type는 String이라, Post의 Type 자료형인 Post.Type(Enum) 타입 변환 필요
        if(request.getTitle()!=null)
            post.setTitle(request.getTitle());
        if(request.getText()!=null)
            post.setText(request.getText());
        if(request.getImageUrls()!=null);
            // 마저 짜기



        return new UpdatePostResponseDTO(
                post.getId(),
                post.getTitle()
        );
    }

    // 게시글 삭제
    @Transactional
    public DeletePostResponseDTO deletePost(Long post_id) {
        postRepository.deleteById(post_id);
        return new DeletePostResponseDTO(post_id);
    }

    // 최신 / 인기 게시글 공용
    public SortedResponseDTO getSortedPosts(Post.Type type, Pageable pageable) {

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

        return new SortedResponseDTO(posts, pageInfo);
    }
}