package com.req2res.actionarybe.domain.comment.entity;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.post.entity.Post;
import com.req2res.actionarybe.global.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 게시글 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String content;

    @Column(name = "is_secret", nullable = false)
    private Boolean isSecret = false;

    // 생성자
    public Comment(Member member, Post post, String content, Boolean isSecret) {
        this.member = member;
        this.post = post;
        this.content = content;
        this.isSecret = Boolean.TRUE.equals(isSecret);  // null이나 false → false, true만 true
    }

    // 수정 메서드
    public void update(String content, Boolean isSecret) {
        if (content != null) this.content = content;
        if (isSecret != null) this.isSecret = isSecret;
    }
}