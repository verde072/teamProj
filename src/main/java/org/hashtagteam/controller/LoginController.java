package org.hashtagteam.controller;

import org.hashtagteam.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/loginPage")
    public String showLoginPage() {
        return "views/login";
    }

    @PostMapping("/tryLogin")
    public String tryLogin(@RequestParam String username, @RequestParam String password, HttpSession session) {
        boolean isAuthenticated = userService.authenticateUser(username, password);

        if (isAuthenticated) {
            session.setAttribute("loggedInUser", username);
            return "index"; // 로그인 성공 시 메인 페이지로 리다이렉트
        } else {
            session.setAttribute("loginError", "Invalid username or password");
            return "views/login"; // 로그인 실패 시 로그인 페이지로 다시 이동
        }
    }
}
