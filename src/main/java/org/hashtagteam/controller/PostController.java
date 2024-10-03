package org.hashtagteam.controller;

import org.hashtagteam.dto.PostDTO;
import org.hashtagteam.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 게시글 목록 조회
    @GetMapping
    public String posts(@RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {
        Map<String,Object> params = new HashMap<>();
        params.put("page",page);
        params.put("size",size);

        List<PostDTO> postList = postService.getPostList(params);
        int PostCount = postService.getPostCount(params);

        model.addAttribute("postList",postList);
        model.addAttribute("PostCount",PostCount);


        return "views/posts";
    }

}
