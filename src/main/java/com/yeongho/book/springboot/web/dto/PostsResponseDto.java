package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.Comment;
import com.yeongho.book.springboot.domain.posts.Post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@NoArgsConstructor
public class PostsResponseDto {

    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime date;
    private List<FileResponseDto> files;
    private int liked;
    private Long viewCount;
    private List<CommentsResponseDto> commentList = new ArrayList<>();

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
        this.commentList = entity.getComment().stream().map(CommentsResponseDto::new).collect(Collectors.toList());
    }
}
