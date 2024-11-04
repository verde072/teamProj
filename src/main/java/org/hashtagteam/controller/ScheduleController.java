package org.hashtagteam.controller;

import org.hashtagteam.dto.ScheduleDTO;
import org.hashtagteam.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // 일정표
    @GetMapping("")
    public String postList(Model model) {

        return "views/schedule";
    }
    
    // 타임테이블에 일정 추가
    @PostMapping("/add")
    public ResponseEntity<ScheduleDTO> addSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        ScheduleDTO savedEvent = scheduleService.addSchedule(scheduleDTO);
        return ResponseEntity.ok(savedEvent);
    }
}
