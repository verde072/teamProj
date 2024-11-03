package org.hashtagteam.service.impl;

import org.hashtagteam.mapper.UserMapper;
import org.hashtagteam.dto.UserDTO;
import org.hashtagteam.model.User;
import org.hashtagteam.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final ModelMapper modelMapper;

    // 생성자
    public UserServiceImpl(UserMapper userMapper, ModelMapper modelMapper) {
        this.userMapper = userMapper;
        this.modelMapper = modelMapper;
    }

    // 사용자 등록 메서드 구현
    @Override
    public void signUpUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);

        userMapper.insertUser(user);
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        int userCount = userMapper.findUser(username, password);

        return userCount == 1;

    }

}
