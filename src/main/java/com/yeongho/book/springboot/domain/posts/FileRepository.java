package com.yeongho.book.springboot.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
