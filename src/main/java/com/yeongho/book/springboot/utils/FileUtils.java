package com.yeongho.book.springboot.utils;


import com.yeongho.book.springboot.domain.posts.File;
import com.yeongho.book.springboot.domain.posts.FileRepository;
import com.yeongho.book.springboot.domain.posts.Posts;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class FileUtils {
    private final FileRepository fileRepository;

    public void store(MultipartFile multipartFile, Posts posts) throws IOException {
        File uploadFile = null;
        if (multipartFile != null && !multipartFile.isEmpty() ) {
            String uuid = UUID.randomUUID().toString();

            uploadFile = File.builder()
                    .originFileName(multipartFile.getOriginalFilename())
                    .uniqueFileName(uuid)
                    .filePath(uuid + "_" + multipartFile.getOriginalFilename())
                    .build();
            uploadFile.setPosts(posts);
            // 파일을 저장한다.
            multipartFile.transferTo(new java.io.File(uploadFile.getFilePath()));
            posts.saveFile(uploadFile);
        }
    }

    public void update(MultipartFile file, Posts posts) throws IOException {
        if (!delete(posts)) {
            throw new IOException("파일 삭제가 정상적으로 이루어지지 않았습니다.");
        }
        store(file, posts);
    }

    public ResponseEntity<Resource> download(File file) throws IOException {
        Path path = Paths.get(file.getFilePath());
        String contentType = Files.probeContentType(path);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(file.getOriginFileName(), StandardCharsets.UTF_8).build());
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        Resource resource = new InputStreamResource(Files.newInputStream(path));
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    public boolean delete(Posts posts) throws IOException {
        fileRepository.delete(posts.getFile());

        // 물리적으로도 삭제
        Path fileToDeletePath = Paths.get(posts.getFile().getFilePath());
        Files.delete(fileToDeletePath);
        posts.saveFile(null);

        boolean isDeleted = !Files.exists(fileToDeletePath);
        return isDeleted;
    }
}
