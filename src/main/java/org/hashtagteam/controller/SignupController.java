package org.hashtagteam.controller;

import org.hashtagteam.dto.UserDTO;
import org.springframework.stereotype.Controller;
import org.hashtagteam.service.UserService;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/signup")
public class SignupController {
    private final UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signupPage")
    public String showSignupPage() {
        return "views/signup";
    }

    @PostMapping("/register")
    public String handleSignup(UserDTO userDTO) {
        if (userDTO != null) {
            userService.signUpUser(userDTO);
        }
        return "redirect:/login/loginPage";
    }

}
