package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.Comments;
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

    public CommentsResponseDto(Comments comments) {
        this.id = comments.getId();
        this.parentId = comments.getParentId();
        this.author = comments.getAuthor();
        this.text = comments.getText();
        this.isDeleted = comments.getIsDeleted();
        this.modifiedDate = comments.getModifiedDate();
        this.liked = comments.getLiked();
    }
}
