package com.req2res.actionarybe.domain.post.entity;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.global.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Post extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Type type;

    @Column(nullable=false)
    private String title;

    @Column
    private String image;

    @Column(nullable=false)
    private String text;

    @Column(nullable = false)
    private int commentsCount;

    @Column(name = "category_id")
    private Long categoryId; // 카테고리 ID (NULL 가능)

    // 상태 enum
    public enum Type {
        인증,
        소통,
        질문,
        구인,
        정보
    }
}
