package com.yeongho.book.springboot.web;

import com.yeongho.book.springboot.domain.posts.Comments;
import com.yeongho.book.springboot.exception.InvalidPasswordException;
import com.yeongho.book.springboot.service.posts.CommentsService;
import com.yeongho.book.springboot.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
public class CommentsApiController {

    private final CommentsService commentsService;

    // 조회
    @GetMapping("/api/v1/comments/{postId}")
    public List<CommentsResponseDto> findAll(@PathVariable Long postId) {
        return commentsService.findAll(postId);
    }

    // 등록
    @PostMapping("/api/v1/comments")
    public CommentsResponseDto save(@RequestBody CommentsSaveRequestDto commentsSaveRequestDto) {
        return commentsService.save(commentsSaveRequestDto);
    }

    // 수정
    @PutMapping("/api/v1/comments/{commentId}")
    public Long update(@PathVariable Long commentId, @RequestBody CommentsUpdateRequestDto requestDto) {
        return commentsService.update(commentId, requestDto);
    }

    // 삭제
    @PostMapping("/api/v1/comments/{commentId}")
    public Long delete(@PathVariable Long commentId, @RequestBody CommentsDeleteDto commentsDeleteDto) throws InvalidPasswordException {
        commentsService.delete(commentId, commentsDeleteDto);
        return commentId;
    }

    @GetMapping("/api/v1/comments/like/{commentId}")
    public LikedDto getLiked(@PathVariable Long commentId) {
        return new LikedDto(commentsService.getLiked(commentId));
    }

    @PostMapping("/api/v1/comments/like/{commentId}")
    public LikedDto addLiked(@PathVariable Long commentId, @RequestBody LikedRequestDto likedRequestDto) {
        return new LikedDto(commentsService.addLiked(commentId, likedRequestDto));
    }

    @PostMapping("/api/v1/comments/unlike/{commentId}")
    public LikedDto deleteLiked(@PathVariable Long commentId, @RequestBody LikedRequestDto ip) {
        return new LikedDto(commentsService.deleteLiked(commentId, ip.getIp()));
    }
}
