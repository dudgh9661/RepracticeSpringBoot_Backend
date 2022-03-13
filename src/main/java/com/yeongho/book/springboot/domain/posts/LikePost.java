package com.yeongho.book.springboot.domain.posts;

import com.yeongho.book.springboot.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class LikePost extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Post post;

    @Column(nullable = false)
    private String ip;

    @Builder
    public LikePost(Post post, String ip) {
        this.post = post;
        this.ip = ip;
    }
}
