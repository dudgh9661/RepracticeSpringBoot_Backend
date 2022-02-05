package com.yeongho.book.springboot.domain.posts;

import com.yeongho.book.springboot.config.WebSecurityConfig;
import com.yeongho.book.springboot.domain.BaseTimeEntity;
import com.yeongho.book.springboot.exception.InvalidPasswordException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ToString
@Getter
@Log4j2
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
//(mappedBy = "posts", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<FileItem> fileItem = new ArrayList<>();

    @Builder
    public Posts(String author, String password, String title, String content) {
        this.author = author;
        this.password = password;
        this.title = title;
        this.content = content;
    }

    public void update(String author, String password, String title, String content, List<MultipartFile> multipartFiles) throws IOException, InvalidPasswordException {
        //작성자와 비밀번호가 일치하면 게시물을 update 한다.
        if (getAuthor().equals(author) && verifyPassword(password)) {
            this.title = title;
            this.content = content;
            deleteFile();
            saveFile(multipartFiles);
        }
    }

    public boolean verifyPassword(String password) throws InvalidPasswordException {
        PasswordEncoder passwordEncoder = new WebSecurityConfig().passwordEncoder();

        if (passwordEncoder.matches(password, this.password)) {
            log.info("사용자가 입력한 비밀번호 일치");
            return true;
        } else {
            // 비밀번호가 일치하지 않을 시, 프론트로 에러코드 발송 필요
            log.info("사용자가 입력한 비밀번호 불일치");
            throw new InvalidPasswordException();
        }
    }

    public void saveFile(List<MultipartFile> multipartFiles) throws IOException {
        if (multipartFiles.isEmpty()) {
            deleteFile();
            return;
        }

        final String absolutePath = "/Users/kim_yeongho/Desktop/fileTestFolder";
        for (MultipartFile multipartFile : multipartFiles) {
            String uuid = UUID.randomUUID().toString();

            FileItem fileItem = FileItem.builder()
                    .originFileName(multipartFile.getOriginalFilename())
                    .uniqueFileName(uuid)
                    .filePath(absolutePath + "/" + uuid + "_" + multipartFile.getOriginalFilename())
                    .build();
            // 파일을 물리적으로 저장한다.
            multipartFile.transferTo(new File(fileItem.getFilePath()));
            getFileItem().add(fileItem);
//            fileItem.setPosts(this);
        }
    }

    public void deleteFile() throws IOException {
        for (FileItem fileItem : this.getFileItem()) {
            // 물리적으로 파일 삭제
            Path pathToDeleteFile = Paths.get(fileItem.getFilePath());
            Files.delete(pathToDeleteFile);
        }
        // 연결된 fileItem을 삭제 -> fileItem이 고아객체가 됨.
        this.getFileItem().clear();
    }
}
