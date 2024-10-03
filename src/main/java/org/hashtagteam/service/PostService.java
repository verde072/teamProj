package org.hashtagteam.service;

import org.hashtagteam.dto.PostDTO;

import java.util.List;
import java.util.Map;

public interface PostService {

    // 게시글 목록 조회
    List<PostDTO> getPostList(Map<String, Object> params);

    // 게시글 개수 조회
    int getPostCount(Map<String, Object> params);

    // 게시글 등록
    PostDTO insertPost(PostDTO postDTO);


}
