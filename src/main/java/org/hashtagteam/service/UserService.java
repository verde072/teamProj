package org.hashtagteam.service;

import org.hashtagteam.dto.UserDTO;

public interface UserService {
    void signUpUser(UserDTO userDTO);
    boolean authenticateUser(String username, String password);
}
