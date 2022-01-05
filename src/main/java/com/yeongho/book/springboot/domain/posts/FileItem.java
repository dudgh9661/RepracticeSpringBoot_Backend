package com.yeongho.book.springboot.domain.posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@ToString
@Getter
@Entity
@NoArgsConstructor
public class FileItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uniqueFileName;

    @Column(nullable = false)
    private String originFileName;

    @Column(nullable = false)
    private String filePath;

    @Column
    private String isDeleted;

    @ManyToOne
    private Posts posts;

    @Builder
    public FileItem(String uniqueFileName, String originFileName, String filePath) {
        this.uniqueFileName = uniqueFileName;
        this.originFileName = originFileName;
        this.filePath = filePath;
    }

    public void setPosts(Posts posts) {
        this.posts = posts;
    }
}
