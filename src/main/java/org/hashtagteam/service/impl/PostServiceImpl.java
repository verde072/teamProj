package org.hashtagteam.service.impl;

import org.hashtagteam.dto.PostDTO;
import org.hashtagteam.mapper.PostMapper;
import org.hashtagteam.model.Post;
import org.hashtagteam.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final ModelMapper modelMapper;

    
    // 생성자
    public PostServiceImpl(PostMapper postMapper, ModelMapper modelMapper) {
        this.postMapper = postMapper;
        this.modelMapper = modelMapper;
    }

    // 게시글 목록 조회 메서드 구현
    @Override
    public List<PostDTO> getPostList(Map<String, Object> params) {
        int page = (int) params.get("page");
        int size = (int) params.get("size");
        int offset = (page - 1) * size;
        params.put("offset", offset);

        return postMapper.getPostList(params);
    }

    @Override
    public int getPostCount(Map<String, Object> params) {
        return postMapper.getPostCount(params);
    }

    // 게시글 등록 메서드 구현
    @Override
    public PostDTO insertPost(PostDTO postDTO) {

        Post post = modelMapper.map(postDTO, Post.class);
        postMapper.insert(post);

        return modelMapper.map(post, PostDTO.class);
    }
}