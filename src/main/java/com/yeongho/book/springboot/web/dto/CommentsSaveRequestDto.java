package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.config.WebSecurityConfig;
import com.yeongho.book.springboot.domain.posts.Comments;
import com.yeongho.book.springboot.domain.posts.Posts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Setter
@Getter
@NoArgsConstructor
public class CommentsSaveRequestDto {
    private String parentId;
    private String postId;
    private String author;
    private String password;
    private String text;
    private Boolean isDeleted;

    public void setPasswordEncoding(String password) {
        // 비밀번호를 암호화해서 저장한다.
        this.password = password;
    }

    @Builder
    public CommentsSaveRequestDto(String parentId, String postId, String author, String password, String text, Boolean isDeleted) {
        this.parentId = parentId;
        this.postId = postId;
        this.author = author;
        this.password = password;
        this.text = text;
        this.isDeleted = isDeleted;
    }

    public Comments toEntity(Posts post) {
        final PasswordEncoder passwordEncoder = new WebSecurityConfig().passwordEncoder();
        return Comments.builder()
                .parentId(Long.parseLong(parentId))
                .post(post)
                .author(author)
                .password(passwordEncoder.encode(password))
                .text(text)
                .isDeleted(isDeleted)
                .build();
    }
}
