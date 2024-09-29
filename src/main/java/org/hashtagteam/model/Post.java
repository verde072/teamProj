package org.hashtagteam.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
// 게시글
public class Post {
    private String postId;       // 게시글아이디
    private String menuId;       // 메뉴아이디
    private String userId;       // 작성자아이디
    private int postNo;          // 글번호
    private String title;        // 제목
    private String content;      // 내용
    private Date createdDate;    // 생성일
    private Date updatedDate;    // 수정일
    private int viewCount;       // 조회수
}
