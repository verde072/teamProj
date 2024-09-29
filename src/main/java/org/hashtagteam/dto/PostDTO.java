package org.hashtagteam.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
// 게시글
public class PostDTO {

    private String postId;      // 게시글 ID
    private Date createdDate;   // 생성일
    private Date updatedDate;   // 수정일
    private int viewCount;      // 조회수
    private String menuId;      // 메뉴 ID
    private String userId;      // 작성자 ID
    private String title;       // 제목
    private String content;     // 내용

}
