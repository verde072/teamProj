package org.hashtagteam.mapper;

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

    List<PostDTO> getPostList(Map<String, Object> params);

    int getPostCount(Map<String, Object> params);

    //TODO userId 세팅필요
    @Insert("INSERT INTO posts (menu_id, post_id, user_id, title, content, created_date, view_count) " +
            "VALUES ('posts', #{postId}, 'id', #{title}, #{content}, SYSDATE(), 0)")
    //@Options(useGeneratedKeys = true, keyProperty = "postId")
    void insertPost(Post post);

    @Insert("INSERT INTO tag (tag_id, tag_nm) " +
            "VALUES (#{tagId}, #{tagNm})")
    // tag 테이블에 insert
    void insertTag(Tag tag);

    @Insert("INSERT INTO postsTag (tag_id, post_id) " +
            "VALUES (#{tagId}, #{postId})")
    // postsTag 테이블에 insert
    void insertPostTag(PostsTag postsTag);

    // 해시태그 존재여부 확인
    @Select("SELECT tag_id FROM tag WHERE tag_nm = #{tagNm}")
    String checkTag(String tagNm);

    PostDTO getPostDetail(String postId);
}
