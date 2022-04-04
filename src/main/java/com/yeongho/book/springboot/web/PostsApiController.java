package com.yeongho.book.springboot.web;

import com.yeongho.book.springboot.service.posts.PostsService;
import com.yeongho.book.springboot.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;

    @GetMapping("/api/v1/posts")
    public List<PostsListResponseDto> findAll() {
        log.info("findAll() 실행");
        return postsService.findAll();
    }

    @GetMapping("/api/v1/posts/search")
    public List<PostsListResponseDto> findByCondition(@RequestParam String searchType, @RequestParam String keyword) {
        log.info("findByCondition() 실행");
        log.info("searchType : " + searchType + " & keyword : " + keyword);
        return postsService.findByCondition(searchType, keyword);
    }

    @GetMapping("/api/v1/posts/page/{page}")
    public PostsListResponseByPagingDto findAllByPage(@PathVariable int page) {
        log.info("findAllByPage() 실행");
        Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "id");
        return postsService.findAllByPage(pageable);
    }

    @GetMapping("/api/v1/posts/page/{page}/search")
    public PostsListResponseByPagingDto findByConditionAndPage(@PathVariable int page, @RequestParam String searchType, @RequestParam String keyword) {
        log.info("findByConditionAndPage() 실행");
        log.info("pageID : " + page + "searchType : " + searchType + " & keyword : " + keyword);
        Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "id");
        return postsService.findByConditionAndPage(pageable, searchType, keyword);
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

    @GetMapping("/api/v1/posts/like/{postId}")
    public LikedDto getLiked(@PathVariable Long postId) {
        return new LikedDto(postsService.getLiked(postId));
    }
    @PostMapping("/api/v1/posts/like/{postId}")
    public LikedDto addLiked(@PathVariable Long postId, @RequestBody LikedRequestDto likedRequestDto) {
        return new LikedDto(postsService.addLiked(postId, likedRequestDto.getIp()));
    }

    @PostMapping("/api/v1/posts/unlike/{postId}")
    public LikedDto deleteLiked(@PathVariable Long postId, @RequestBody LikedRequestDto likedRequestDto) {
        return new LikedDto(postsService.deleteLiked(postId, likedRequestDto.getIp()));
    }

    @GetMapping("/api/v1/posts/likeStatus/{postId}/ip/{ip}")
    public LikedResponseDto getLikeStatus(@PathVariable Long postId, @PathVariable String ip) {
        return postsService.getLikeStatus(postId, ip);
    }
}
