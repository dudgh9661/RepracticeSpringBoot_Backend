package com.yeongho.book.springboot.domain.posts;

import com.yeongho.book.springboot.exception.FileException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Entity
@NoArgsConstructor
public class FileItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uniqueFileName;

    @Column(nullable = false)
    private String originFileName;

    @Column(nullable = false)
    private String filePath;

    @ManyToOne
    private Posts post;

    @Builder
    public FileItem(String uniqueFileName, String originFileName, String filePath) {
        this.uniqueFileName = uniqueFileName;
        this.originFileName = originFileName;
        this.filePath = filePath;
    }

    public void setPosts(Posts post) {
        this.post = post;
    }

    public ResponseEntity<Resource> download() throws FileException, IOException {
        Path path = Paths.get(this.getFilePath());
        String contentType = Files.probeContentType(path);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);
        Resource resource = new InputStreamResource(Files.newInputStream(path));
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
