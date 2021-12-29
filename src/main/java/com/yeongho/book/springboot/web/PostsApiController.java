package com.yeongho.book.springboot.web;

import com.yeongho.book.springboot.service.posts.FileService;
import com.yeongho.book.springboot.service.posts.PostsService;
import com.yeongho.book.springboot.web.dto.PostsListResponseDto;
import com.yeongho.book.springboot.web.dto.PostsResponseDto;
import com.yeongho.book.springboot.web.dto.PostsSaveRequestDto;
import com.yeongho.book.springboot.web.dto.PostsUpdateRequestDto;
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
    public Long save(@RequestPart(value="data") PostsSaveRequestDto postsSaveRequestDto, @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        return postsService.save(postsSaveRequestDto, file);
    }

    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestPart(value="data") PostsUpdateRequestDto postsUpdateRequestDto, @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        return postsService.update(id, postsUpdateRequestDto, file);
    }

    @GetMapping("/api/v1/posts/{id}")
    public PostsResponseDto findById (@PathVariable Long id) {
        return postsService.findById(id);
    }

    @DeleteMapping("/api/v1/posts/{id}")
    public Long delete(@PathVariable Long id) throws IOException {
        postsService.delete(id);
        return id;
    }

    @GetMapping("/api/v1/posts/download/{id}")
    public ResponseEntity<Resource> fileDownload(@PathVariable Long id) throws IOException {
        return postsService.fileDownload(id);
    }
}
