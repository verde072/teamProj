package org.hashtagteam.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.hashtagteam.dto.ScheduleDTO;
import org.hashtagteam.service.ScheduleService;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // 일정표
    @GetMapping("")
    public String schedule() {

        return "views/schedule";
    }

    // 일정 조회
    @GetMapping("/get")
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules(HttpSession session) {
        String userId = (String) session.getAttribute("loggedInUser");
        List<ScheduleDTO> schedules = scheduleService.getAllSchedules(userId);
        return ResponseEntity.ok(schedules); // JSON 형식으로 일정 목록 반환
    }

    // 일정 추가
    @PostMapping("/add")
    public ResponseEntity<?> addSchedule(HttpSession session, @RequestBody ScheduleDTO scheduleDTO) {
        try {
            scheduleDTO.setUserId((String) session.getAttribute("loggedInUser"));
            scheduleService.addSchedule(scheduleDTO);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Schedule added successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 일정 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSchedule(HttpSession session,
                                            @RequestParam String startTime,
                                            @RequestParam String endTime) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            String userId = (String) session.getAttribute("loggedInUser");
            LocalDateTime parsedStartTime = LocalDateTime.parse(startTime, formatter);
            LocalDateTime parsedEndTime = LocalDateTime.parse(endTime, formatter);

            ScheduleDTO scheduleDTO = new ScheduleDTO();
            scheduleDTO.setEndTime(parsedEndTime);
            scheduleDTO.setStartTime(parsedStartTime);
            scheduleDTO.setUserId(userId);

            // 삭제 시도
            boolean isDeleted = scheduleService.delete(scheduleDTO);

            if (isDeleted) {
                return ResponseEntity.ok("일정 삭제 완료");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제할 일정을 찾을 수 없음");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청: " + e.getMessage());
        }

    }

    // 일정 변경
    @PutMapping("/update")
    public ResponseEntity<?> updateSchedule(HttpSession session, @RequestBody ScheduleDTO scheduleDTO) {
        // 기존 일정 삭제
        scheduleDTO.setUserId((String) session.getAttribute("loggedInUser"));
        boolean isDeleted = scheduleService.delete(scheduleDTO);

        // 새로운 일정 삽입
        if (isDeleted) {
            scheduleService.addSchedule(scheduleDTO);
            return ResponseEntity.ok("일정 업데이트 완료");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("업데이트할 일정을 찾을 수 없음");
        }
    }

    // 일정 AI 자동생성
    @PostMapping("/generate")
    public ResponseEntity<?> generateSchedule(@RequestBody Map<String, String> request) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        String apiUrl = "https://api.openai.com/v1/chat/completions";
        String prompt = request.get("prompt");

        try {
            RestTemplate restTemplate = new RestTemplate();

            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey); // Authorization 설정
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청 바디 생성
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> messages = Map.of(
                    "model", "gpt-4",
                    "messages", List.of(
                            Map.of("role", "system", "content", "당신은 여행 일정표를 JSON 형식으로 생성하는 AI입니다. 모든 응답은 반드시 다음 JSON 형식을 따라야 합니다. descriptions 은 10자 이내로 하루에 3-4군데 정도 가는 일정으로 짜줘. 중간에 점심 저녁도 포함해서. 일정은 최소 1시간이어야 돼  { \"travelEvents\": [ { \"title\": \"일정 제목\", \"start\": \"YYYY-MM-DDTHH:mm:ss\", \"end\": \"YYYY-MM-DDTHH:mm:ss\", \"description\": \"일정 설명\" } ] }. 응답 외에는 어떠한 추가 설명도 포함하지 마세요."),
                            Map.of("role", "user", "content", prompt)
                    ),
                    "max_tokens", 1000
            );

            String requestBody = objectMapper.writeValueAsString(messages);

            // HTTP 요청 보내기
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            // 응답 본문 출력
            String responseBody = response.getBody();
            System.out.println("API 응답: " + responseBody);

            // OpenAI 응답에서 content 부분 추출 및 JSON 파싱
            String rawContent = objectMapper.readTree(responseBody)
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            System.out.println("OpenAI 응답 content: " + rawContent);

            Map<String, Object> travelEvents = objectMapper.readValue(rawContent, new TypeReference<Map<String, Object>>() {});

            return ResponseEntity.ok(travelEvents);
        } catch (HttpClientErrorException e) {
            System.err.println("OpenAI API 호출 실패: " + e.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OpenAI API 호출 실패: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("일정 생성 중 오류 발생");
        }
    }

}
