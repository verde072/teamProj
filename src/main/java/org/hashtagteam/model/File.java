package org.hashtagteam.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
// 게시글_해시태그
public class File {
    private String fileId;                    // 파일아이디
    private String postId;                    // 게시글아이디
    private String originalFileNm;            // 파일명
    private String saveFileNm;                // 파일명
    private String savePath;                  // 파일경로
    private String fileType;                  // 파일타입
    private int fileSize;                     // 파일사이즈
    private LocalDateTime createdDate;
}
