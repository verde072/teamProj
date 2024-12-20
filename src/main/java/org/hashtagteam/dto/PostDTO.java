package org.hashtagteam.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
// 게시글
public class PostDTO {
    private String postId;                  // 게시글 ID
    private LocalDateTime createdDate;      // 생성일
    private LocalDateTime updatedDate;      // 수정일
    private int viewCount;                  // 조회수
    private String menuId;                  // 메뉴 ID
    private String userId;                  // 작성자 ID
    private String title;                   // 제목
    private String content;                 // 내용
    private List<String> imgUrls;           // 이미지 리스트
    private String imgUrl;                  // 대표이미지
    private List<String> hashtags;          // 해시태그 리스트
}
