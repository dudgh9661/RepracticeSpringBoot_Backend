package com.yeongho.book.springboot.domain.posts;

import com.yeongho.book.springboot.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class LikedComments extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Comments comment;

    @ManyToOne
    private Posts post;

    @Column(nullable = false)
    private String ip;

    @Builder
    public LikedComments(Comments comment, String ip, Posts post) {
        this.comment = comment;
        this.ip = ip;
        this.post = post;
    }
}
