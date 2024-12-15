package org.hashtagteam.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
// 일정디테일
public class Schedule {
    private String scheduleId;
    private String userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String title;
    private String content;
    private String color;
}
