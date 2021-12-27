package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.File;
import com.yeongho.book.springboot.domain.posts.Posts;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FileSaveDto {
    private String uniqueFileName;
    private String originFileName;
    private String filePath;
    private Posts posts;

    public File toEntity() {
        return File.builder()
                .uniqueFileName(uniqueFileName)
                .originFileName(originFileName)
                .filePath(filePath)
                .build();
    }

    @Builder
    public FileSaveDto(String uniqueFileName, String originFileName, String filePath, Posts posts) {
        this.uniqueFileName = uniqueFileName;
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.posts = posts;
    }
}
