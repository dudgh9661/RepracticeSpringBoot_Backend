package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.File;
import com.yeongho.book.springboot.domain.posts.Posts;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class PostsResponseDto {

    private Long id;
    private String title;
    private String content;
    private String author;
    private FileResponseDto responseFile;

    public PostsResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
        if (entity.getFile() != null) {
            this.responseFile = new FileResponseDto(entity.getFile());
        }
    }
}
