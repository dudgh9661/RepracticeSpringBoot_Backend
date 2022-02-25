package com.yeongho.book.springboot.domain.posts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostsRepository extends JpaRepository<Posts, Long> {

    Page<Posts> findByTitleContaining(Pageable pageable, String keyword);
    List<Posts> findByTitleContaining(String keyword);

    Page<Posts> findByContentContaining(Pageable pageable, String keyword);
    List<Posts> findByContentContaining(String keyword);

    Page<Posts> findByAuthorContaining(Pageable pageable, String keyword);
    List<Posts> findByAuthorContaining(String keyword);
}
