package com.yeongho.book.springboot.service.posts;


import com.yeongho.book.springboot.domain.posts.FileItem;
import com.yeongho.book.springboot.domain.posts.FileRepository;
import com.yeongho.book.springboot.domain.posts.Posts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;

    @Value("${spring.servlet.multipart.location}")
    private String absolutePath;

    public void store(MultipartFile multipartFile, Posts posts) throws IOException {
        FileItem uploadFileItem = null;
        if (multipartFile != null && !multipartFile.isEmpty() ) {
            String uuid = UUID.randomUUID().toString();

            uploadFileItem = FileItem.builder()
                    .originFileName(multipartFile.getOriginalFilename())
                    .uniqueFileName(uuid)
                    .filePath(absolutePath + "/" + uuid + "_" + multipartFile.getOriginalFilename())
                    .build();
            uploadFileItem.setPosts(posts);
            // 파일을 저장한다.
            multipartFile.transferTo(new File(uploadFileItem.getFilePath()));
            posts.saveFile(uploadFileItem);
        }
    }

    public void update(MultipartFile file, Posts posts) throws IOException {
        // 기존 게시글에 파일이 존재하는 경우 && file이 null 값으로 들어오는 경우 == 파일에 대한 수정사항이 없다는 것
        if (posts.getFileItem() != null && file == null) return;
        if (!delete(posts)) {
                throw new IOException("파일 삭제가 정상적으로 이루어지지 않았습니다.");
        }
        store(file, posts);
    }

    public ResponseEntity<Resource> download(FileItem fileItem) throws IOException {
        try {
            Path path = Paths.get(fileItem.getFilePath());
            System.out.println("File path ::: " + path);
            String contentType = Files.probeContentType(path);
            HttpHeaders headers = new HttpHeaders();
//            headers.setContentDisposition(ContentDisposition.builder("attachment")
//                    .filename(fileItem.getUniqueFileName()+'_'+fileItem.getOriginFileName(), StandardCharsets.UTF_8)
//                    .build());
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);

            Resource resource = new InputStreamResource(Files.newInputStream(path));
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    public boolean delete(Posts posts) throws IOException {

        // update 시, 기존에 파일이 존재하지 않았을 경우 삭제할 것이 없으므로 true를 반환
        if (posts.getFileItem() == null) return true;
        fileRepository.delete(posts.getFileItem());

        // 물리적으로도 삭제
        Path pathToDeleteFile = Paths.get(posts.getFileItem().getFilePath());
        Files.delete(pathToDeleteFile);
        posts.saveFile(null);

        boolean isDeleted = !Files.exists(pathToDeleteFile);
        return isDeleted;
    }
}
