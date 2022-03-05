package com.yeongho.book.springboot.service.posts;

import com.yeongho.book.springboot.domain.posts.*;
import com.yeongho.book.springboot.exception.InvalidPasswordException;
import com.yeongho.book.springboot.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class CommentsService {

    private final PostsRepository postsRepository;
    private final CommentsRepository commentsRepository;
    private final LikedCommentsRepository likedCommentsRepository;

    @Transactional(readOnly = true)
    public List<CommentsResponseDto> findAll(Long postId) {
        Posts post = postsRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다 id = " + postId));
        return commentsRepository.findAllByPost(post).stream()
                .map(CommentsResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentsResponseDto save(CommentsSaveRequestDto commentsSaveRequestDto) {
        Long postId = Long.parseLong(commentsSaveRequestDto.getPostId());
        Posts post = postsRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + postId));
        Comments comment = commentsSaveRequestDto.toEntity(post);
        commentsRepository.save(comment);
        return new CommentsResponseDto(comment);
    }

    @Transactional
    public Long update(Long commentId, CommentsUpdateRequestDto commentsUpdateRequestDto) throws InvalidPasswordException {
        Comments comments = commentsRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        comments.update(commentsUpdateRequestDto);
        return commentId;
    }

    @Transactional
    public void delete(Long commentId, CommentsDeleteDto commentsDeleteDto) throws InvalidPasswordException {
        Comments comments = commentsRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        comments.delete(commentsDeleteDto);
    }

    public int getLiked(Long commentId) {
        Comments comments = commentsRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        return comments.getLiked();
    }

    @Transactional
    public int addLiked(Long commentId, LikedRequestDto likedRequestDto) {
        String ip = likedRequestDto.getIp();
        Long postId = likedRequestDto.getPostId();
        log.info("Comment addLiked 시작 ::: commentId : " + commentId + " ip : " + ip);
        // 1. 어떤 게시글의 좋아요 인지 확인한다.
        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        // 2. 어떤 댓글인지 확인한다.
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        // 3. 어떤 ip인지 확인한다.
        // 4. likedComments Table에 저장한다.
        likedCommentsRepository.save(LikedComments.builder()
                .comment(comment)
                .ip(ip)
                .post(post)
                .build());
        // 5. 댓글 좋아요를 +1 한다.
        log.info("Comment deleteLiked 종료");
        return comment.addLike();
    }

    @Transactional
    public int deleteLiked(Long commentId, String ip) {
        log.info("Comment deleteLiked 시작 ::: commentId : " + commentId + " ip : " + ip);
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        log.info("deleteLiked comment ::: " + comment);
        LikedComments likedComments = likedCommentsRepository.findByCommentAndIp(comment, ip).get();
        likedCommentsRepository.delete(likedComments);
        log.info("Comment deleteLiked 종료, 삭제된 LikedComments : " + likedComments);
        return comment.deleteLike();
    }
}
