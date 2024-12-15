package org.hashtagteam.controller;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hashtagteam.dto.PostDTO;
import org.hashtagteam.service.SearchService;
import org.hashtagteam.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    // 태그 기반 게시글 목록 조회
    @GetMapping("/tag")
    public String getPostsByTag(@RequestParam String tag,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "8") int size,
                                Model model) {
        Map<String, Object> params = new HashMap<>();
        params.put("tag", tag);
        params.put("page", page);
        params.put("size", size);

        List<PostDTO> postList = searchService.getPostsByTag(tag);
        int postCount = searchService.getPostCountByTag(tag);

        model.addAttribute("postList", postList);
        model.addAttribute("postCount", postCount);
        model.addAttribute("tag", tag);

        return "views/search";
    }

    // 게시글 목록 조회
    @GetMapping("/list")
    public String postList(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "8") int size,
                           @RequestParam(required = false) String key,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false) LocalDate startDt,
                           @RequestParam(required = false) LocalDate endDt,
                           @RequestParam(required = false) String hashtags,
                           @RequestParam(required = false) String tag,
                           Model model) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("key", key);
        params.put("keyword", keyword);
        params.put("startDt", startDt);
        params.put("endDt", endDt);
        if (hashtags != null) params.put("hashtags", getHashtagList(hashtags));
        if (tag != null) params.put("tag", tag);

        List<PostDTO> postList = searchService.getPostList(params);
        int postCount = searchService.getPostCount(params);

        model.addAttribute("postList", postList);
        model.addAttribute("postCount", postCount);

        // 검색조건 유지
        model.addAttribute("key", key);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDt", startDt);
        model.addAttribute("endDt", endDt);
        model.addAttribute("hashtags", hashtags);
        model.addAttribute("tag", tag);

        return "views/search";
    }

    // 게시글 등록/수정 페이지
    @GetMapping("/write")
    public String writePost(@RequestParam(required = false) String postId, Model model) {
        PostDTO postDTO;

        if (postId == null) { // 신규 작성 모드: 빈 PostDTO 생성
            postDTO = new PostDTO();
        } else { // 수정 모드: 기존 게시글 데이터를 가져옴
            postDTO = searchService.getPostDetail(postId);
            String hashtags = String.join(",", postDTO.getHashtags());
            model.addAttribute("hashtags", hashtags);
        }

        model.addAttribute("post", postDTO);

        return "views/write-post";
    }


    @ResponseBody
    @PostMapping("/upload")
    public ResponseEntity<?> imageUpload(@RequestParam("upload") MultipartFile image) {
        String uploadDir = new File("src/main/resources/static/images/upload").getAbsolutePath();
        String fileName = image.getOriginalFilename();

        try {
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(image.getInputStream(), filePath);

            String imageUrl = "/uploads/" + fileName;
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/read")
    public String readPost(@RequestParam String postId, Model model) {
        PostDTO postDTO = searchService.getPostDetail(postId);
        model.addAttribute("post", postDTO);
        String hashtags = String.join(",", postDTO.getHashtags());
        model.addAttribute("hashtags", hashtags);

        return "views/read-post";
    }

    @PostMapping("/submit")
    public String submitPost(@ModelAttribute PostDTO postDTO,
                             @RequestParam(required = false) String postId,
                             @RequestParam(required = false) String hashtags) {

        if(postId == null || postId.isEmpty()){
            postDTO.setPostId(IdGenerator.generateId("P"));
            postDTO.setHashtags(getHashtagList(hashtags));
            searchService.insert(postDTO);
        }else{
            searchService.update(postDTO);
        }


        return "redirect:/search";
    }

    @DeleteMapping("/delete")
    public String deletePost(@ModelAttribute PostDTO postDTO) {
        searchService.delete(postDTO);

        return "redirect:/search";
    }

    // tagify로 받은 해시태그목록 json 형태 > list 형태로 바꿔서 반환
    public List<String> getHashtagList(String hashtags) {
        List<String> hashtagList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<Map<String, String>> list = objectMapper.readValue(hashtags, new TypeReference<List<Map<String, String>>>() {
            });

            hashtagList = list.stream()
                    .map(map -> map.get("value"))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("hashtags 파싱 중 에러", e);
        }

        return hashtagList;
    }
}
