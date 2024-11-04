package org.hashtagteam.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
// 일정표
public class ScheduleDTO {
    private String schduleId;
    private String userId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
