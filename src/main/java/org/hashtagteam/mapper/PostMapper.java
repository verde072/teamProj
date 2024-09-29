package org.hashtagteam.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.hashtagteam.dto.PostDTO;
import org.hashtagteam.model.Post;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {

    List<PostDTO> getPostList(Map<String, String> params);


    @Insert("INSERT INTO posts (menu_id, user_id, title, content, created_date, view_count) " +
            "VALUES (#{menuId}, #{userId}, #{title}, #{content}, #{createdDate}, #{viewCount})")
    @Options(useGeneratedKeys = true, keyProperty = "postId")
    Post insert(Post post);
}
