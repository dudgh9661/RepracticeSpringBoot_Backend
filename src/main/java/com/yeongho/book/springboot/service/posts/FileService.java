package com.yeongho.book.springboot.service.posts;

import com.yeongho.book.springboot.domain.posts.File;
import com.yeongho.book.springboot.domain.posts.FileRepository;
import com.yeongho.book.springboot.domain.posts.Posts;
import com.yeongho.book.springboot.web.dto.FileResponseDto;
import com.yeongho.book.springboot.web.dto.FileSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileService {
    private final FileRepository fileRepository;

    @Transactional
    public Long save(File uploadFile) {
        return fileRepository.save(uploadFile).getId();
    }

    public FileResponseDto findById (Long id) {
        File entity = fileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 파일이 존재하지 않습니다. id = " + id));
        return new FileResponseDto(entity);
    }
}
