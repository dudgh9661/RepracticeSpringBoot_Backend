package com.yeongho.book.springboot.web;

import com.yeongho.book.springboot.domain.posts.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin("*")
@RequiredArgsConstructor
public class FileApiController {

    private FileRepository fileRepository;

    // 파일 업로드

}
