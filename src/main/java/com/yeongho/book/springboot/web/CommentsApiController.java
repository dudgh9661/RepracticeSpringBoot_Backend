package com.yeongho.book.springboot.web;

import com.yeongho.book.springboot.service.posts.CommentsService;
import com.yeongho.book.springboot.web.dto.CommentsDeleteDto;
import com.yeongho.book.springboot.web.dto.CommentsSaveRequestDto;
import com.yeongho.book.springboot.web.dto.CommentsResponseDto;
import com.yeongho.book.springboot.web.dto.CommentsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public Long save(@RequestBody CommentsSaveRequestDto commentsSaveRequestDto) {
        return commentsService.save(commentsSaveRequestDto);
    }

    // 수정
    @PutMapping("/api/v1/comments/{commentId}")
    public Long update(@PathVariable Long commentId, @RequestBody CommentsUpdateRequestDto requestDto) {
        return commentsService.update(commentId, requestDto);
    }

    // 삭제
    @PostMapping("/api/v1/comments/{commentId}")
    public Long delete(@PathVariable Long commentId, @RequestBody CommentsDeleteDto commentsDeleteDto) {
        commentsService.delete(commentId, commentsDeleteDto);
        return commentId;
    }
}
