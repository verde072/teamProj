package org.hashtagteam.controller;

import org.hashtagteam.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.hashtagteam.service.UserService;

@Controller
public class SignupController {
    private final UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String showSignupPage() {
        return "views/signup";
    }

    @PostMapping("/signup")
    public String handleSignup(User user) {
        if (user != null) {
            userService.signUpUser(user);
        }
        return "redirect:/login";
    }

}
