package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.Comments;
import com.yeongho.book.springboot.domain.posts.Posts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentsSaveRequestDto {

    private Long parentId;
    private Long postId;
    private String author;
    private String password;
    private String comment;

    public void setPasswordEncoding(String password) {
        // 비밀번호를 암호화해서 저장한다.
        this.password = password;
    }

    @Builder
    public CommentsSaveRequestDto(Long parentId, Long postId, String author, String password, String comment) {
        this.parentId = parentId;
        this.postId = postId;
        this.author = author;
        this.password = password;
        this.comment = comment;
    }

    public Comments toEntity(Posts post) {
        return Comments.builder()
                .parentId(parentId)
                .post(post)
                .author(author)
                .password(password)
                .comment(comment)
                .build();
    }
}
