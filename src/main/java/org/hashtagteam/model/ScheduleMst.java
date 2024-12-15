package org.hashtagteam.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
// 일정마스터
public class ScheduleMst {
    private String scheduleId;
    private String userId;
    private Integer day;
    private Integer cost;
    private String destination;
    private String date;  // 필요없는 필드, 제거 예정
}
