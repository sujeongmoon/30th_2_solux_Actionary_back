package com.req2res.actionarybe.domain.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_image")
@Getter
@NoArgsConstructor
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private int imageOrder;

    public PostImage(Post post, String imageUrl, int imageOrder) {
        this.post = post;
        this.imageUrl = imageUrl;
        this.imageOrder = imageOrder;
    }
}