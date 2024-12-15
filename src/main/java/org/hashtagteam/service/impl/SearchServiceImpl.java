package org.hashtagteam.service.impl;

import org.hashtagteam.dto.PostDTO;
import org.hashtagteam.mapper.PostMapper;
import org.hashtagteam.model.Post;
import org.hashtagteam.model.PostsTag;
import org.hashtagteam.model.Tag;
import org.hashtagteam.service.PostService;
import org.hashtagteam.service.SearchService;
import org.hashtagteam.utils.IdGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl  implements SearchService {
    private final PostMapper postMapper;
    private final ModelMapper modelMapper;

    // 생성자
    public SearchServiceImpl(PostMapper postMapper, ModelMapper modelMapper) {
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
        insertPostTags(postDTO);
    }

    // 게시글 상세조회 메서드 구현
    @Override
    public PostDTO getPostDetail(String postId) {
        return postMapper.getPostDetail(postId);
    }

    @Override
    public void delete(PostDTO postDTO) {
        postMapper.deletePost(postDTO.getPostId());

        // 삭제할 태그 리스트 얻기 - 태그가 사용된 게시물이 없으면 태그도 삭제
        List<String> tagsToDelete = postDTO.getHashtags().stream()
                .filter(tagNm -> postMapper.countPostsByTag(tagNm) == 0)
                .collect(Collectors.toList());

        if (!tagsToDelete.isEmpty()) {  // 일괄 삭제
            postMapper.deleteTags(tagsToDelete);
        }

    }

    @Override
    public void update(PostDTO postDTO) {
        Post post = modelMapper.map(postDTO, Post.class);
        postMapper.updatePost(post);

        // 태그 리스트 전부 삭제 후 다시 추가
        postMapper.deleteTagsByPostId(postDTO.getPostId());
        insertPostTags(postDTO);

    }

    // [추가] 태그 기반 게시글 목록 조회
    @Override
    public List<PostDTO> getPostsByTag(String tag) {
        return postMapper.getPostsByTag(tag);
    }

    // [추가] 태그 기반 게시글 개수 조회
    @Override
    public int getPostCountByTag(String tag) {
        return postMapper.countPostsByTagName(tag);
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

    // posts_tag 테이블에 태그리스트 삽입
    public void insertPostTags(PostDTO postDTO){
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
}
