package com.yeongho.book.springboot.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentsRepository extends JpaRepository<Comments, Long> {
    List<Comments> findAllByPost(Posts post);
}
