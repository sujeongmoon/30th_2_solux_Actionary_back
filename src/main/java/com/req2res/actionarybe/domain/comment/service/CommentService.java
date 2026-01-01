package com.req2res.actionarybe.domain.comment.service;

import com.req2res.actionarybe.domain.comment.dto.AuthorCommentDTO;
import com.req2res.actionarybe.domain.comment.dto.CreateCommentRequestDTO;
import com.req2res.actionarybe.domain.comment.dto.CreateCommentResponseDTO;
import com.req2res.actionarybe.domain.comment.entity.Comment;
import com.req2res.actionarybe.domain.comment.repository.CommentRepository;
import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.post.entity.Post;
import com.req2res.actionarybe.domain.post.repository.PostRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

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
}
