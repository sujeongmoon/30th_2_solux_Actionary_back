package com.req2res.actionarybe.domain.post.entity;

import com.req2res.actionarybe.domain.comment.entity.Comment;
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

    // PostImage 정보도 객체로 다룰 수 있게 해줌 (PostImage만 Post 존재 아는건 비효율적이니)
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    private List<PostImage> images = new ArrayList<>();
    public void addImage(PostImage image) {
        images.add(image);
    }


    // Comment 정보도 객체로 다룰 수 있게 해줌 (Comment만 Post 존재 아는건 비효율적이니)
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    private List<Comment> comments = new ArrayList<>();
    public void addComment(Comment comment) {comments.add(comment);}


    public Post(Member member, Type type, String title, String text) {
        this.member = member;
        this.type = type;
        this.title = title;
        this.text = text;
        this.commentsCount = 0;
    }

    public enum Type {
        인증,
        소통,
        질문,
        구인,
        정보
    }

    // 필요 정보만 바꿀 수 있도록 일부러 @Getter 안 씀
    // 단점: 코드 길어짐 (but, 4줄),
    public void setType(Type type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void increaseCommentsCount() {
        this.commentsCount++;
    }
    public void decreaseCommentsCount() {
        if(this.commentsCount > 0) {
            this.commentsCount--;
        }
    }

}