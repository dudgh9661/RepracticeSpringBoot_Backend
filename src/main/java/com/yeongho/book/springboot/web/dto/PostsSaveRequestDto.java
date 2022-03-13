package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.config.WebSecurityConfig;
import com.yeongho.book.springboot.domain.posts.Post;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@ToString
@Getter
@Setter
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

    public Post toEntity() {
        final PasswordEncoder passwordEncoder = new WebSecurityConfig().passwordEncoder();
        return Post.builder()
                .author(author)
                .password(passwordEncoder.encode(password))
                .title(title)
                .content(content)
                .build();
    }
}
