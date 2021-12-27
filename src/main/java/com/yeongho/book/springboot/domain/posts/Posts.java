package com.yeongho.book.springboot.domain.posts;

import com.yeongho.book.springboot.config.WebSecurityConfig;
import com.yeongho.book.springboot.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ToString
@Getter
@NoArgsConstructor
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

    @OneToOne(mappedBy = "posts", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, optional = true)
    private File file;

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
        if (getAuthor().equals(author) && passwordEncoder.matches(password, this.getPassword())) {
            System.out.println("비밀번호가 일치함");
            this.title = title;
            this.content = content;
        } else {
            // 비밀번호가 일치하지 않을 시, 프론트로 에러코드 발송 필요
            System.out.println("비밀번호 불일치함");
            System.out.println("해당 게시물 비밀번호 : " + this.getPassword());
            System.out.println("Client로부터 받은 비밀번호 : " + password);
            throw new IllegalStateException("비밀번호가 틀렸습니다");
        }
    }

    public void saveFile(File uploadFile) {
        this.file = uploadFile;
    }
}
