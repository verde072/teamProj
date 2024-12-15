package org.hashtagteam.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.hashtagteam.dto.PostDTO;
import org.hashtagteam.model.Post;
import org.hashtagteam.model.PostsTag;
import org.hashtagteam.model.Tag;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {

    // 게시글 목록 조회
    List<PostDTO> getPostList(Map<String, Object> params);

    // 게시글 개수 조회
    int getPostCount(Map<String, Object> params);

    //TODO userId 세팅필요
    @Insert("INSERT INTO posts (menu_id, post_id, user_id, title, content, created_date, view_count) " +
            "VALUES ('posts', #{postId}, #{userId}, #{title}, #{content}, NOW(), 0)")
    //@Options(useGeneratedKeys = true, keyProperty = "postId")
    void insertPost(Post post);

    // tag 테이블에 insert
    @Insert("INSERT INTO tag (tag_id, tag_nm) " +
            "VALUES (#{tagId}, #{tagNm})")
    void insertTag(Tag tag);

    // postsTag 테이블에 insert
    @Insert("INSERT INTO posts_tag (tag_id, post_id) " +
            "VALUES (#{tagId}, #{postId})")
    void insertPostTag(PostsTag postsTag);

    // 해시태그 존재여부 확인
    @Select("SELECT tag_id FROM tag WHERE tag_nm = #{tagNm}")
    String checkTag(String tagNm);

    // 게시글 상세 정보 조회
    PostDTO getPostDetail(String postId);

    // 게시글 수정
    void updatePost(Post post);

    // 게시글 삭제
    @Delete("DELETE FROM posts WHERE post_id = #{postId} ")
    void deletePost(String postId);

    // 해시태그 일괄 삭제
    void deleteTags(List<String> tagNames);

    // 해시태그 사용한 게시글 수 조회
    int countPostsByTag (String tagNm);

    // 게시글 관련 해시태그 전부 삭제
    @Delete("DELETE FROM posts_tag WHERE post_id = #{postId}")
    void deleteTagsByPostId(String postId);
}
