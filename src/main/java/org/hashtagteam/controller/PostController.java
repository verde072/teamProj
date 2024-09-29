package org.hashtagteam.controller;

import org.hashtagteam.dto.PostDTO;
import org.hashtagteam.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }


    @GetMapping
    public String posts() {
        System.out.println("getPostList");

        return "posts";
    }

    // 게시글 목록 조회 API
    @GetMapping("/list")
    public ResponseEntity<List<PostDTO>> getPostList(@RequestParam Map<String, String> params) {
        List<PostDTO> postList = postService.getPostList(params);
        return ResponseEntity.ok(postList);
    }
}
