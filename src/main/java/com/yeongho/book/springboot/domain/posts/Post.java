package com.yeongho.book.springboot.domain.posts;

import com.yeongho.book.springboot.config.WebSecurityConfig;
import com.yeongho.book.springboot.domain.BaseTimeEntity;
import com.yeongho.book.springboot.exception.FileException;
import com.yeongho.book.springboot.exception.InvalidPasswordException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Log4j2
@NoArgsConstructor
@Entity
public class Post extends BaseTimeEntity {

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
    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<FileItem> fileItem = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> commen = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<LikePost> likedPosts = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<LikeComment> LikeComment = new ArrayList<>();

    @Column
    private int liked;

    @Column
    private Long viewCount;

    @Builder
    public Post(String author, String password, String title, String content) {
        this.author = author;
        this.password = password;
        this.title = title;
        this.content = content;
        this.liked = 0; // liked init
        this.viewCount = 0L;
    }

    public void update(String author, String password, String title, String content, List<MultipartFile> multipartFiles) throws FileException, InvalidPasswordException {
        //작성자와 비밀번호가 일치하면 게시물을 update 한다.
        log.info("게시글 업데이트 시작");
        if (getAuthor().equals(author) && verifyPassword(password)) {
            this.title = title;
            this.content = content;

            if (multipartFiles == null || multipartFiles.isEmpty()) { // 빈 업로드를 보낸 경우
                if (this.getFileItem().isEmpty()) return; // 기존 저장된 파일이 없는 경우 return
                deleteFile(this.getFileItem());
            } else if (this.getFileItem().isEmpty()) { // server에 저장된 file이 없는 경우
                saveFile(multipartFiles);
            } else { // 업로드가 필요한 파일만 필터링하여 업로드한다.
                List<MultipartFile> saveFileItem = new ArrayList<>();
                List<FileItem> deleteFileItem = new ArrayList<>();

                for (MultipartFile m : multipartFiles) {
                    String uploadFileName = m.getOriginalFilename();
                    boolean findUploadFile = true;
                    for (FileItem fileItem : this.getFileItem()) {
                        if (uploadFileName.equals(fileItem.getOriginFileName())) {
                            findUploadFile = false;
                            break;
                        }
                    }
                    if (findUploadFile) saveFileItem.add(m);
                }

                for (FileItem fileItem : this.getFileItem()) {

                    String deleteFile = fileItem.getOriginFileName();
                    boolean findDeleteFile = true;
                    for (MultipartFile m : multipartFiles) {
                        if (deleteFile.equals(m.getOriginalFilename())) {
                            findDeleteFile = false;
                            break;
                        }
                    }
                    if (findDeleteFile) deleteFileItem.add(fileItem);
                }

                deleteFile(deleteFileItem);
                saveFile(saveFileItem);
            }
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

    public void saveFile(List<MultipartFile> multipartFiles) throws FileException {
        log.info("파일 저장 시작");
        File fileConfig = new File("files");
        log.info("파일 저장 경로 : " + fileConfig.getAbsolutePath());
        log.info("폴더 존재 여부 : " + fileConfig.getAbsoluteFile().exists());
        if (!fileConfig.getAbsoluteFile().exists()) {
            fileConfig.getAbsoluteFile().mkdirs();
            log.info(fileConfig.getAbsolutePath() + " 경로에 폴더가 존재하지 않습니다. 폴더를 생성합니다.");
            log.info(fileConfig.getPath() + " 경로에 폴더가 존재하지 않습니다. 폴더를 생성합니다.");
        }
        for (MultipartFile multipartFile : multipartFiles) {
            String uuid = UUID.randomUUID().toString();

            FileItem fileItem = FileItem.builder()
                    .originFileName(multipartFile.getOriginalFilename())
                    .uniqueFileName(uuid)
                    .filePath(fileConfig.getAbsolutePath() + "/" + uuid + "_" + multipartFile.getOriginalFilename())
                    .build();
            // 파일을 물리적으로 저장한다.
            try {
                multipartFile.transferTo(new File(fileItem.getFilePath()));
            } catch (Exception e) {
                log.info("파일이 정상적으로 저장되지 않았습니다." + e.toString());
                throw new FileException();
            }
            log.info("파일 저장 진행중 ::: " + fileItem.getOriginFileName());
            this.getFileItem().add(fileItem);
//            log.info()
            fileItem.setPosts(this);
        }
    }

    public void deleteFile(List<FileItem> fileItemList) throws FileException {
        log.info("파일 삭제 시작");
        List<FileItem> deleteFileItem = new ArrayList<>(fileItemList);
        int size = deleteFileItem.size();
        for (int i = 0; i < size; i++) {
            FileItem fileItem = deleteFileItem.get(i);
            // 물리적으로 파일 삭제
            log.info("파일 삭제 진행중 ::: " + fileItem.getFilePath() );
            Path pathToDeleteFile = Paths.get(fileItem.getFilePath());
            try {
                Files.delete(pathToDeleteFile);
                log.info("삭제된 파일 ::: " + fileItem.getFilePath());
            } catch (Exception e) {
                log.error("파일 삭제 과정에서 에러 발생 ::: " + e);
                throw new FileException();
            }
            // fileItem List에서도 삭제

            int deleteFileIdx = this.getFileItem().indexOf(fileItem);
            if (deleteFileIdx != -1) {
                // 연결된 fileItem을 삭제 -> fileItem이 고아객체가 됨.
                this.getFileItem().remove(deleteFileIdx);
            }
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

    public void addViewCount() {
        this.viewCount++;
    }
}
