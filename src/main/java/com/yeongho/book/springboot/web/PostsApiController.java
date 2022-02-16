package com.yeongho.book.springboot.web;

import com.yeongho.book.springboot.service.posts.PostsService;
import com.yeongho.book.springboot.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Log4j2
@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;

    @GetMapping("/api/v1/posts")
    public List<PostsListResponseDto> findAll() {
        return postsService.findAll();
    }

    @GetMapping("/api/v1/posts/search")
    public List<PostsListResponseDto> findByCondition(@RequestParam String searchType, @RequestParam String keyword) {
        log.info("searchType : " + searchType + " & keyword : " + keyword);
        return postsService.findByCondition(searchType, keyword);
    }

    @PostMapping("/api/v1/posts")
    public Long save(@RequestPart(value="data") PostsSaveRequestDto postsSaveRequestDto, @RequestPart(value = "file", required = false) List<MultipartFile> files) {
        return postsService.save(postsSaveRequestDto, files);
    }

    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestPart(value="data") PostsUpdateRequestDto postsUpdateRequestDto, @RequestPart(value = "file", required = false) List<MultipartFile> files) {
        return postsService.update(id, postsUpdateRequestDto, files);
    }

    @GetMapping("/api/v1/posts/{id}")
    public PostsResponseDto findById (@PathVariable Long id) {
        return postsService.findById(id);
    }

    @PostMapping("/api/v1/posts/{id}")
    public void delete(@PathVariable Long id, @RequestBody PostsDeleteDto postsDeleteDto) {
        postsService.delete(id, postsDeleteDto);
    }

    @GetMapping("/api/v1/posts/download/{id}")
    public ResponseEntity<Resource> fileDownload(@PathVariable Long id) {
        return postsService.fileDownload(id);
    }
}
