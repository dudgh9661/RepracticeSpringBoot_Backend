package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.FileItem;
import com.yeongho.book.springboot.domain.posts.Posts;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@ToString
@Getter
public class PostsResponseDto {

    private Long id;
    private String title;
    private String content;
    private String author;
    private FileResponseDto file;

    public PostsResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
        if (entity.getFileItem() != null) {
            this.file = new FileResponseDto(entity.getFileItem());
        }
    }
}
