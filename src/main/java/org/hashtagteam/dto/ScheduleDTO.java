package org.hashtagteam.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
// 일정표
public class ScheduleDTO {
    private String scheduleId;
    private String userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime oldStartTime;
    private LocalDateTime oldEndTime;
    private String title;
    private String color;
}
