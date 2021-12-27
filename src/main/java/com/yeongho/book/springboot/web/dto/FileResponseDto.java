package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.File;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FileResponseDto {
    private Long id;
    private String uniqueFileName;
    private String originFileName;
    private String filePath;

    @Builder
    public FileResponseDto (File file) {
        this.id = file.getId();
        this.uniqueFileName = file.getUniqueFileName();
        this.originFileName = file.getOriginFileName();
        this.filePath = file.getFilePath();
    }
}
