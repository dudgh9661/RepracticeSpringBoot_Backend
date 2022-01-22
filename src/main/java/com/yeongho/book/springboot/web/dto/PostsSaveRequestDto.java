package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.config.WebSecurityConfig;
import com.yeongho.book.springboot.domain.posts.Posts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        final PasswordEncoder passwordEncoder = new WebSecurityConfig().passwordEncoder();
        return Posts.builder()
                .author(author)
                .password(passwordEncoder.encode(password))
                .title(title)
                .content(content)
                .build();
    }
}
