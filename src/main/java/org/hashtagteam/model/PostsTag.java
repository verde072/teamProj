package org.hashtagteam.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
// 게시글_해시태그
public class PostsTag {
    private String tagId;                     // 태그아이디
    private String postId;                    // 게시글아이디
}
