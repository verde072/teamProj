package org.hashtagteam.service.impl;

import org.hashtagteam.dto.PostDTO;
import org.hashtagteam.mapper.PostMapper;
import org.hashtagteam.model.Post;
import org.hashtagteam.model.PostsTag;
import org.hashtagteam.model.Tag;
import org.hashtagteam.service.PostService;
import org.hashtagteam.utils.IdGenerator;
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
    public void insert(PostDTO postDTO) {
        Post post = modelMapper.map(postDTO, Post.class);
        postMapper.insertPost(post);

        // 해시태그가 있는 경우 해시태그 리스트를 삽입
        if (postDTO.getHashtags() != null && !postDTO.getHashtags().isEmpty()) {
            for (String tagNm : postDTO.getHashtags()) {

                PostsTag postsTag = new PostsTag();
                postsTag.setPostId(postDTO.getPostId());
                postsTag.setTagId(getOrCreateTagId(tagNm));
                postMapper.insertPostTag(postsTag);
            }
        }
    }

    // 게시글 상세조회 메서드 구현
    @Override
    public PostDTO getPostDetail(String postId) {
        return postMapper.getPostDetail(postId);
    }

    // tagId 반환, 없으면 새로 생성
    public String getOrCreateTagId(String tagNm) {
        // 해시태그가 존재하는지 확인하고, 있으면 id 반환
        String existingTagId = postMapper.checkTag(tagNm);
        if (existingTagId != null) {
            return existingTagId;
        }

        // 해시태그가 존재하지 않으면 새로 생성하고, id 반환
        Tag tag = new Tag();
        tag.setTagId(IdGenerator.generateId("HT"));
        tag.setTagNm(tagNm);
        postMapper.insertTag(tag);

        return tag.getTagId();
    }

}