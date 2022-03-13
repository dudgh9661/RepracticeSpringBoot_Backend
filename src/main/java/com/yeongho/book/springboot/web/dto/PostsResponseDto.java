package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.Post;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
public class PostsResponseDto {

    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime date;
    private List<FileResponseDto> files;
    private int liked;
    private Long viewCount;

    public PostsResponseDto(Post entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
        this.date = entity.getModifiedDate();
        this.files = entity.getFileItem() == null ? null : entity.getFileItem().stream()
                .map(FileResponseDto::new).collect(Collectors.toList());
        this.liked = entity.getLiked();
        this.viewCount = entity.getViewCount();
    }
}
