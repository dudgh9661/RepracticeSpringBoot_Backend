package com.yeongho.book.springboot.domain.posts;

import com.yeongho.book.springboot.config.WebSecurityConfig;
import com.yeongho.book.springboot.domain.BaseTimeEntity;
import com.yeongho.book.springboot.exception.InvalidPasswordException;
import com.yeongho.book.springboot.web.dto.CommentsDeleteDto;
import com.yeongho.book.springboot.web.dto.CommentsUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Log4j2
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
    private String text;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private Boolean isDeleted;

    @Column
    private int liked;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<LikedComments> LikedComments = new ArrayList<>();

    @Builder
    public Comments(Long parentId, Posts post, String author, String password, String text, Boolean isDeleted) {
        this.parentId = parentId;
        this.post = post;
        this.author = author;
        this.password = password;
        this.text = text;
        this.isDeleted = isDeleted;
        this.liked = 0; // 좋아요 Init
    }

    public void update(CommentsUpdateRequestDto requestDto) throws InvalidPasswordException {
        if (verifyPassword(requestDto.getPassword())) {
            this.text = requestDto.getText();
        }
    }

    public void delete(CommentsDeleteDto commentsDeleteDto) throws InvalidPasswordException {
        if (verifyPassword(commentsDeleteDto.getPassword())) {
            this.isDeleted = true;
        }
    }
    public boolean verifyPassword(String password) throws InvalidPasswordException {
        PasswordEncoder passwordEncoder = new WebSecurityConfig().passwordEncoder();

        if (passwordEncoder.matches(password, this.password)) {
            log.info("사용자가 입력한 비밀번호 일치");
            return true;
        } else {
            log.info("사용자가 입력한 비밀번호 불일치");
            throw new InvalidPasswordException();
        }
    }

    public int addLike() {
        this.liked++;
        return this.getLiked();
    }

    public int deleteLike() {
        this.liked--;
        return this.getLiked();
    }
}
