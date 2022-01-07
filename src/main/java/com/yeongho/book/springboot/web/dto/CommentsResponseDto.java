package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.Comments;
import com.yeongho.book.springboot.domain.posts.Posts;
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
    private String comment;
    private LocalDateTime modifiedDate;

    public CommentsResponseDto(Comments comments) {
        this.id = comments.getId();
        this.parentId = comments.getParentId();
        this.author = comments.getAuthor();
        this.comment = comments.getComment();
        this.modifiedDate = comments.getModifiedDate();
    }
}
