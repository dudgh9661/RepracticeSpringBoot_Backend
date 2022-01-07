package com.yeongho.book.springboot.domain.posts;

import com.yeongho.book.springboot.config.WebSecurityConfig;
import com.yeongho.book.springboot.domain.BaseTimeEntity;
import com.yeongho.book.springboot.web.dto.CommentsUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Comments extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글 테이블 PK

    @Column(nullable = true)
    private Long parentId; // 상위 댓글 ID

    @ManyToOne
    private Posts post;

    @Column(nullable = false)
    private String author;

    @Column(length = 500, nullable = false)
    private String password;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;

    @Builder
    public Comments(Long parentId, Posts post, String author, String password, String comment) {
        this.parentId = parentId;
        this.post = post;
        this.author = author;
        this.password = password;
        this.comment = comment;
    }

    public void update(CommentsUpdateRequestDto requestDto) {
        WebSecurityConfig webSecurityConfig = new WebSecurityConfig();
        PasswordEncoder passwordEncoder = webSecurityConfig.passwordEncoder();

        if (correctPassword(requestDto.getPassword())) {
            this.comment = requestDto.getComment();
        }
    }

    public boolean correctPassword(String password) {
        PasswordEncoder passwordEncoder = new WebSecurityConfig().passwordEncoder();
        if (passwordEncoder.matches(password, this.password)) {
            System.out.println("댓글 수정 비밀번호 일치");
            return true;
        } else {
            System.out.println("댓글 수정 비밀번호 불일치");
            System.out.println("해당 댓글 비밀번호 : " + this.password);
            System.out.println("Client로부터 받은 비밀번호 : " + password);
            throw new IllegalStateException("비밀번호가 틀렸습니다");
        }
    }
}
