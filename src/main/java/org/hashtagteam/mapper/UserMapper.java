package org.hashtagteam.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.hashtagteam.dto.UserDTO;
import org.hashtagteam.model.User;


@Mapper
public interface UserMapper {

    // 사용자 등록
    @Insert("INSERT INTO user (user_id, password, name, gender, birth, phone, email, primary_addr, detail_addr, created_date) " +
            "VALUES (#{userId}, #{password}, #{name}, #{gender}, #{birth}, #{phone}, #{email}, #{primaryAddr}, #{detailAddr}, SYSDATE())")
    void insertUser(User user);

}
