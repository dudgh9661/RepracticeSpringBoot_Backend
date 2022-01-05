package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.FileItem;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FileResponseDto {
    private Long id;
    private String uniqueFileName;
    private String originFileName;
    private String filePath;

    @Builder
    public FileResponseDto (FileItem fileItem) {
        this.id = fileItem.getId();
        this.uniqueFileName = fileItem.getUniqueFileName();
        this.originFileName = fileItem.getOriginFileName();
        this.filePath = fileItem.getFilePath();
    }
}
