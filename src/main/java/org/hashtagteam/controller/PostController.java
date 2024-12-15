package org.hashtagteam.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.hashtagteam.dto.PostDTO;
import org.hashtagteam.service.PostService;
import org.hashtagteam.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Controller
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);


    public PostController(PostService postService) {
        this.postService = postService;
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
                           Model model) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("key", key);
        params.put("keyword", keyword);
        params.put("startDt", startDt);
        params.put("endDt", endDt);
        if (hashtags != null) params.put("hashtags", getHashtagList(hashtags));

        int postCount = postService.getPostCount(params);
        List<PostDTO> postList = postService.getPostList(params);

        // 페이지네이션
        int totalPages = (int) Math.ceil((double) postCount / size);
        int pageGroupSize = 10;
        int currentGroup = (int) Math.ceil((double) page / pageGroupSize);
        int startPage = (currentGroup - 1) * pageGroupSize + 1;
        int endPage = Math.min(startPage + pageGroupSize - 1, totalPages);

        // 다음/이전 그룹 여부
        boolean hasPreviousGroup = startPage > 1;
        boolean hasNextGroup = endPage < totalPages;

        model.addAttribute("postList", postList);
        model.addAttribute("postCount", postCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage-1);
        model.addAttribute("hasPreviousGroup", hasPreviousGroup);
        model.addAttribute("hasNextGroup", hasNextGroup);

        // 검색조건 유지
        model.addAttribute("key", key);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDt", startDt);
        model.addAttribute("endDt", endDt);
        model.addAttribute("hashtags", hashtags);

        return "views/post-list";
    }

    // 게시글 등록/수정 페이지
    @GetMapping("/write")
    public String writePost(@RequestParam(required = false) String postId, Model model) {
        PostDTO postDTO;

        if (postId == null) { // 신규 작성 모드: 빈 PostDTO 생성
            postDTO = new PostDTO();
        } else { // 수정 모드: 기존 게시글 데이터를 가져옴
            postDTO = postService.getPostDetail(postId);
            String hashtags = String.join(",", postDTO.getHashtags());
            model.addAttribute("hashtags", hashtags);
        }

        model.addAttribute("post", postDTO);

        return "views/write-post";
    }


    // chkeditor 이미지 업로드
    @ResponseBody
    @PostMapping("/upload")
    public ResponseEntity<?> imageUpload(@RequestParam("file") MultipartFile file) {
        try {
            // 저장 경로
            String uploadDir = new File("src/main/resources/static/images/upload").getAbsolutePath() + File.separator;
            String randomFileName = IdGenerator.generateId("fn");
            String originalFileName = file.getOriginalFilename();
            String newFileName = randomFileName + "_" + originalFileName;

            File uploadFile = new File(uploadDir + newFileName);
            file.transferTo(uploadFile);

            // 파일 저장 확인 //TODO 업로드시 editor에서 깨지는 오류 해결 필요
            if (!uploadFile.exists()) {
                throw new IOException("File save failed");
            }

            // 저장 완료 후 URL 반환
            String fileUrl = "/images/upload/" + newFileName;
            return ResponseEntity.ok(Map.of(
                    "uploaded", true,
                    "url", fileUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("uploaded", false, "error", Map.of("message", e.getMessage())));
        }
    }

    @GetMapping("/read")
    public String readPost(@RequestParam String postId, Model model, HttpSession session) {
        PostDTO postDTO = postService.getPostDetail(postId);
        model.addAttribute("post", postDTO);
        String hashtags = String.join(",", postDTO.getHashtags());
        model.addAttribute("hashtags", hashtags);
        model.addAttribute("loginId" , (String) session.getAttribute("loggedInUser"));

        return "views/read-post";
    }

    // 게시글 등록
    @PostMapping("/submit")
    public String submitPost(@ModelAttribute PostDTO postDTO,
                             @RequestParam(required = false) String postId,
                             @RequestParam(required = false) String hashtags,
                             HttpSession session) throws MalformedURLException {
        postDTO.setUserId((String) session.getAttribute("loggedInUser"));

        if (postId == null || postId.isEmpty()) {
            postDTO.setPostId(IdGenerator.generateId("P"));
            postDTO.setHashtags(getHashtagList(hashtags));
            postDTO.setImgUrls(extractImageUrls(postDTO.getContent()));
            postService.insert(postDTO);
        } else {
            postDTO.setHashtags(getHashtagList(hashtags));
            postDTO.setImgUrls(extractImageUrls(postDTO.getContent()));
            postService.update(postDTO);
        }


        return "redirect:/post/list";
    }

    // 게시글 삭제
    @GetMapping("/delete")
    public String deletePost(@ModelAttribute PostDTO postDTO,
                             HttpSession session) {
        postDTO.setUserId((String) session.getAttribute("loggedInUser"));

        postService.delete(postDTO);

        return "redirect:/post/list";
    }


    // 여행후기 AI 자동생성
    @PostMapping("generate")
    public ResponseEntity<?> generatePost(@RequestBody Map<String, String> request) {

        String apiKey = System.getenv("OPENAI_API_KEY");
        String url = "https://api.openai.com/v1/chat/completions";
        String prompt = request.get("prompt");

        try {
            RestTemplate restTemplate = new RestTemplate();

            // OpenAI 요청 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + apiKey);
            headers.add("Content-Type", "application/json");

            // 요청 바디 생성
            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", List.of(Map.of("role", "user", "content", "주제: " + prompt + "\n" +
                            "여행 후기를 쓰면되는데 CKEditor5에서 사용할 HTML 형식으로 만들어줘."
                            + " 내용이 길면 주제별로 문단 구분을 추가하고, 문단마다 제목을 굵게 강조해 생성해줘. "
                            + " 글자 크기를 적절히 사용하여 시각적으로 깔끔하게 구성해줘.")),
                    "max_tokens", 2000, // 생성될 글자 수
                    "temperature", 0.7 // 창의성 수준
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // OpenAI API 호출
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            // OpenAI 응답 처리
            Map<String, Object> choices = (Map<String, Object>) ((List<?>) response.getBody().get("choices")).get(0);
            String generatedText = (String) ((Map<String, Object>) choices.get("message")).get("content");

            return ResponseEntity.ok(Map.of("post", generatedText.trim()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "게시글 생성 중 오류가 발생했습니다."));
        }
    }

    // 해시태그 ai 자동생성
    @PostMapping("/generate-tags")
    public ResponseEntity<?> generateTags(@RequestBody Map<String, String> request) {
        String review = request.get("review");

        try {
            String apiKey = System.getenv("OPENAI_API_KEY");
            // OpenAI API 요청
            String apiUrl = "https://api.openai.com/v1/chat/completions";
            RestTemplate restTemplate = new RestTemplate();

            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // 요청 바디 생성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", "당신은 후기에 적합한 해시태그를 추천하는 AI입니다."),
                    Map.of("role", "user", "content", review + "에 적합한 해시태그를 추천해주세요.")
            ));
            requestBody.put("max_tokens", 50);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // API 호출
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

            // 응답 처리
            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String rawContent = (String) message.get("content");
            String[] tags = rawContent.replaceAll("[#]", "").split("\\s+");

            return ResponseEntity.ok(Map.of("tags", tags));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("해시태그 생성 중 오류 발생");
        }
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


    // imgUrl 추출
    private List<String> extractImageUrls(String content) {
        List<String> imageUrls = new ArrayList<>();
        try {
            Document document = Jsoup.parse(content);
            Elements imgElements = document.select("img");

            for (Element img : imgElements) {
                String src = img.attr("src");
                imageUrls.add(src);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageUrls;
    }
}
