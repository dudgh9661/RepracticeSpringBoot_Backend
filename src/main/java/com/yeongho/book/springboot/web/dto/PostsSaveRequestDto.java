package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.File;
import com.yeongho.book.springboot.domain.posts.Posts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class PostsSaveRequestDto {

    private String author;
    private String password;
    private String title;
    private String content;

    public void makePasswordEncoding(String password) {
        this.password = password;
    }

    @Builder
    public PostsSaveRequestDto(String author, String password, String title, String content) {
        this.author = author;
        this.password = password;
        this.title = title;
        this.content = content;
    }

    public Posts toEntity() {
        return Posts.builder()
                .author(author)
                .password(password)
                .title(title)
                .content(content)
                .build();
    }
}
