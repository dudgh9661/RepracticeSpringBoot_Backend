package com.yeongho.book.springboot.domain.posts;

import com.yeongho.book.springboot.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class LikedPosts extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Posts post;

    @Column(nullable = false)
    private String ip;

    @Builder
    public LikedPosts(Posts post, String ip) {
        this.post = post;
        this.ip = ip;
    }
}
