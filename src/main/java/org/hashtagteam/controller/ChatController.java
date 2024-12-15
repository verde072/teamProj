package org.hashtagteam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/chat")
public class ChatController {

    @GetMapping
    public String openChat(@RequestParam String userId) {
        return "views/chat-popup";
    }
}
