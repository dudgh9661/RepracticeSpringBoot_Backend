package com.yeongho.book.springboot.web;

import com.yeongho.book.springboot.service.posts.FileService;
import com.yeongho.book.springboot.service.posts.PostsService;
import com.yeongho.book.springboot.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;
    private final FileService fileService;

    @GetMapping("/api/v1/posts")
    public List<PostsListResponseDto> findAll() {
        return postsService.findAll();
    }

    @PostMapping("/api/v1/posts")
    public Long save(@RequestPart(value="data") PostsSaveRequestDto postsSaveRequestDto, @RequestPart(value = "file", required = false) List<MultipartFile> files) throws IOException {
        System.out.println("multipart size :::::: " + files.size());
        return postsService.save(postsSaveRequestDto, files);
    }

    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestPart(value="data") PostsUpdateRequestDto postsUpdateRequestDto, @RequestPart(value = "file", required = false) List<MultipartFile> files) throws IOException {
        return postsService.update(id, postsUpdateRequestDto, files);
    }

    @GetMapping("/api/v1/posts/{id}")
    public PostsResponseDto findById (@PathVariable Long id) {
        return postsService.findById(id);
    }

    @PostMapping("/api/v1/posts/{id}")
    public Long delete(@PathVariable Long id, @RequestBody PostsDeleteDto postsDeleteDto) throws IOException {
        postsService.delete(id, postsDeleteDto);
        return id;
    }

    @GetMapping("/api/v1/posts/download/{id}")
    public ResponseEntity<Resource> fileDownload(@PathVariable Long id) throws IOException {
        return postsService.fileDownload(id);
    }
}
