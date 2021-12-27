package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.Comments;
import com.yeongho.book.springboot.domain.posts.Posts;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentsResponseDto {
    private Long id;
    private Long parentId;
    private Posts posts;
    private String author;
    private String password;
    private String comment;
    private LocalDateTime modifiedDate;

    public CommentsResponseDto(Comments comments) {
        this.id = comments.getId();
        this.parentId = comments.getParentId();
        this.posts = comments.getPost();
        this.author = comments.getAuthor();
        this.password = comments.getPassword();
        this.comment = comments.getComment();
        this.modifiedDate = comments.getModifiedDate();
    }
}
