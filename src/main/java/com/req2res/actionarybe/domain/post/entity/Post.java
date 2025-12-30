package com.req2res.actionarybe.domain.post.entity;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.global.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private int commentsCount;

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    private List<PostImage> images = new ArrayList<>();

    public Post(Member member, Type type, String title, String text) {
        this.member = member;
        this.type = type;
        this.title = title;
        this.text = text;
        this.commentsCount = 0;
    }

    // PostImage 정보도 객체로 다룰 수 있게 해줌 (PostImage만 Post 존재 아는건 비효율적이니)
    public void addImage(PostImage image) {
        images.add(image);
    }

    public enum Type {
        인증,
        소통,
        질문,
        구인,
        정보
    }
}