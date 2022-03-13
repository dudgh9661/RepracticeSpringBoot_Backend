package com.yeongho.book.springboot.domain.posts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByTitleContaining(Pageable pageable, String keyword);
    List<Post> findByTitleContaining(String keyword);

    Page<Post> findByContentContaining(Pageable pageable, String keyword);
    List<Post> findByContentContaining(String keyword);

    Page<Post> findByAuthorContaining(Pageable pageable, String keyword);
    List<Post> findByAuthorContaining(String keyword);
}
