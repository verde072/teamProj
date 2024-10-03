package org.hashtagteam.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
// 게시글
public class Post {
    private String postId;                      // 게시글아이디
    private String menuId;                      // 메뉴아이디
    private String userId;                      // 작성자아이디
    private String title;                       // 제목
    private String content;                     // 내용
    private LocalDateTime  createdDate;         // 생성일
    private LocalDateTime updatedDate;          // 수정일
    private int viewCount;                      // 조회수
}
