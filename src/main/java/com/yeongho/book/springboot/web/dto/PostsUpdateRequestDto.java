package com.yeongho.book.springboot.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class PostsUpdateRequestDto {
    private String author;
    private String password;
    private String title;
    private String content;

    @Builder
    public PostsUpdateRequestDto(String author, String password, String title, String content) {
        this.author = author;
        this.password = password;
        this.title = title;
        this.content = content;
    }
}
