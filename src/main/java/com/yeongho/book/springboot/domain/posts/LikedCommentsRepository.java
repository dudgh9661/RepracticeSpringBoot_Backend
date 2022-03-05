package com.yeongho.book.springboot.domain.posts;

import com.yeongho.book.springboot.web.dto.CommentsLikedResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikedCommentsRepository extends JpaRepository<LikedComments, Long> {
    Optional<LikedComments> findByCommentAndIp(Comments comment, String ip);
    Optional<List<LikedComments>> findAllByCommentAndIp(Comments comment, String ip);
    Optional<List<LikedComments>> findAllByPostAndIp(Posts post, String ip);
}
