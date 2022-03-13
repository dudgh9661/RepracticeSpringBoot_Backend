package com.yeongho.book.springboot.domain.posts;

import com.yeongho.book.springboot.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class LikeComment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Comment comment;

    @ManyToOne
    private Post post;

    @Column(nullable = false)
    private String ip;

    @Builder
    public LikeComment(Comment comment, String ip, Post post) {
        this.comment = comment;
        this.ip = ip;
        this.post = post;
    }
}
