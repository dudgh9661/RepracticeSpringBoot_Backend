package com.yeongho.book.springboot.domain.posts;

import com.yeongho.book.springboot.config.WebSecurityConfig;
import com.yeongho.book.springboot.domain.BaseTimeEntity;
import com.yeongho.book.springboot.web.dto.PostsDeleteDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
public class Posts extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(length = 500, nullable = false)
    private String password;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "posts", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<FileItem> fileItem;

    @Builder
    public Posts(String author, String password, String title, String content) {
        this.author = author;
        this.password = password;
        this.title = title;
        this.content = content;
    }

    public void update(String author, String password, String title, String content) {
        System.out.println("update test!");
        WebSecurityConfig webSecurityConfig = new WebSecurityConfig();
        PasswordEncoder passwordEncoder = webSecurityConfig.passwordEncoder();
        //작성자와 비밀번호가 일치하면 게시물을 update 한다.
        if (getAuthor().equals(author) && correctPassword(password)) {
            this.title = title;
            this.content = content;
        }
    }

    public boolean correctPassword(String password) {
        PasswordEncoder passwordEncoder = new WebSecurityConfig().passwordEncoder();

        if (passwordEncoder.matches(password, this.password)) {
            System.out.println("비밀번호가 일치함");
            return true;
        } else {
            // 비밀번호가 일치하지 않을 시, 프론트로 에러코드 발송 필요
            System.out.println("비밀번호 불일치함");
            System.out.println("해당 게시물 비밀번호 : " + this.password);
            System.out.println("Client로부터 받은 비밀번호 : " + password);
            throw new IllegalStateException("비밀번호가 틀렸습니다");
        }
    }

    public void saveFile(List<FileItem> uploadFileItem) {
        this.fileItem = uploadFileItem;
    }
}
