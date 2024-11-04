package org.hashtagteam.mapper;

import org.apache.ibatis.annotations.*;

import org.hashtagteam.model.User;


@Mapper
public interface UserMapper {

    // 사용자 등록
    @Insert("INSERT INTO user (password, name, gender, birth, phone, email, primary_addr, detail_addr, created_date) " +
            "VALUES (#{password}, #{name}, #{gender}, #{birth}, #{phone}, #{email}, #{primaryAddr}, #{detailAddr}, SYSDATE())")
    void insertUser(User user);


    @Select("SELECT COUNT(*) FROM user WHERE email = #{username} AND password = #{password}")
    int findUser(@Param("username") String username, @Param("password") String password);
}
