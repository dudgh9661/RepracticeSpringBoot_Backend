package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
public class CommentsResponseDto {
    private Long id;
    private Long parentId;
    private String author;
    private String text;
    private Boolean isDeleted;
    private LocalDateTime modifiedDate;
    private int liked;

    public CommentsResponseDto(Comment comment) {
        this.id = comment.getId();
        this.parentId = comment.getParentId();
        this.author = comment.getAuthor();
        this.text = comment.getText();
        this.isDeleted = comment.getIsDeleted();
        this.modifiedDate = comment.getModifiedDate();
        this.liked = comment.getLiked();
    }
}
