package com.yeongho.book.springboot.web;

import com.yeongho.book.springboot.service.posts.PostsService;
import com.yeongho.book.springboot.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;

    @GetMapping("/api/v1/posts")
    public List<PostsListResponseDto> findAll() {
        return postsService.findAll();
    }

    @PostMapping("/api/v1/posts")
    public Long save(@RequestPart(value="data") PostsSaveRequestDto postsSaveRequestDto, @RequestPart(value = "file", required = false) List<MultipartFile> files) {
        try {
            return postsService.save(postsSaveRequestDto, files);
        } catch (IOException e) {
            // log로 에러를 출력한다.
            // client에게 에러를 래핑하여 전달한다.
            return 0L;
        }
    }

    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestPart(value="data") PostsUpdateRequestDto postsUpdateRequestDto, @RequestPart(value = "file", required = false) List<MultipartFile> files) throws IOException {
        try {
            return postsService.update(id, postsUpdateRequestDto, files);
        } catch (IOException e) {
            return 0L;
        }
    }

    @GetMapping("/api/v1/posts/{id}")
    public PostsResponseDto findById (@PathVariable Long id) {
        return postsService.findById(id);
    }

    @PostMapping("/api/v1/posts/{id}")
    public Long delete(@PathVariable Long id, @RequestBody PostsDeleteDto postsDeleteDto) throws IOException {
        try {
            postsService.delete(id, postsDeleteDto);
            return 0L;
        } catch (IOException e) {
            return 0L;
        }
    }

    @GetMapping("/api/v1/posts/download/{id}")
    public ResponseEntity<Resource> fileDownload(@PathVariable Long id) throws IOException {
        try {
            return postsService.fileDownload(id);
        } catch (IOException e) {
           return null;
        }
    }
}
