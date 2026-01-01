package com.req2res.actionarybe.domain.comment.service;

import com.req2res.actionarybe.domain.comment.dto.*;
import com.req2res.actionarybe.domain.comment.entity.Comment;
import com.req2res.actionarybe.domain.comment.repository.CommentRepository;
import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.post.dto.PageInfoDTO;
import com.req2res.actionarybe.domain.post.entity.Post;
import com.req2res.actionarybe.domain.post.repository.PostRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 댓글 생성
    @Transactional
    public CreateCommentResponseDTO  createComment(Long post_id, CreateCommentRequestDTO request, Long member_id){
        Post post = postRepository.findById(post_id)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));
        Member member = memberRepository.findById(member_id)
                .orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Comment comment = new Comment(
                member,
                post,
                request.getContent(),
                request.getIsSecret()
        );
        commentRepository.save(comment);
        post.addComment(comment); // post에도 연결
        post.increaseCommentsCount(); // Post의 commentCount++

        AuthorCommentDTO author = new AuthorCommentDTO(
                comment.getMember().getId(),
                comment.getMember().getNickname()
        );

        return new CreateCommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                comment.getIsSecret(),
                comment.getCreatedAt(),
                author
        );
    }

    // 댓글 조회 (특정 게시글)
    public GetCommentResponseDTO getCommentsByPostId(Long post_id, Pageable pageable){
        Post post =  postRepository.findById(post_id)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));

        // 페이지네이션 정보
        Page<Comment> page = commentRepository.findByPost(post, pageable);
        PageInfoDTO pageInfo=new PageInfoDTO(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        // 댓글 정보 (페이지네이션의 결과인 page의 10개씩 잘린 정보 == page.getContent()) (post.getComments 써서 오류났었음..)
        List<CommentInfoDTO> comments = page.getContent().stream()
                .map(comment -> new CommentInfoDTO(
                        comment.getId(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        comment.getIsSecret(),
                        new CommentInfoDTO.AuthorDTO(
                                comment.getMember().getId(),
                                comment.getMember().getNickname(),
                                comment.getMember().getProfileImageUrl(),
                                comment.getMember().getBadge().getId()
                        )
                ))
                .toList();

        return new GetCommentResponseDTO(
                comments,
                pageInfo
        );
    }
}
