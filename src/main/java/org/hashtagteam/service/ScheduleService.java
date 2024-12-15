package org.hashtagteam.service;

import org.hashtagteam.dto.ScheduleDTO;

import java.util.List;


public interface ScheduleService {

    // 스케줄 조회
    List<ScheduleDTO> getAllSchedules(String userId);

    // 스케줄 저장
    void addSchedule(ScheduleDTO scheduleDTO);
    
    // 스케줄 삭제
    boolean delete(ScheduleDTO scheduleDTO);

}
