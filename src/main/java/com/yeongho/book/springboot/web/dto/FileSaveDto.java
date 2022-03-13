package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.FileItem;
import com.yeongho.book.springboot.domain.posts.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FileSaveDto {
    private String uniqueFileName;
    private String originFileName;
    private String filePath;
    private Post post;

    public FileItem toEntity() {
        return FileItem.builder()
                .uniqueFileName(uniqueFileName)
                .originFileName(originFileName)
                .filePath(filePath)
                .build();
    }

    @Builder
    public FileSaveDto(String uniqueFileName, String originFileName, String filePath, Post post) {
        this.uniqueFileName = uniqueFileName;
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.post = post;
    }
}
