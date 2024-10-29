package org.hashtagteam.service.impl;

import org.hashtagteam.mapper.UserMapper;
import org.hashtagteam.model.User;
import org.hashtagteam.service.UserService;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper; // UserMapper 주입

    // 생성자
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // 사용자 등록 메서드 구현
    @Override
    public void signUpUser(User user) {
        userMapper.insertUser(user);
    }

}
