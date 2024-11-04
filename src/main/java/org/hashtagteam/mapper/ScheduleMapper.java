package org.hashtagteam.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.hashtagteam.dto.ScheduleDTO;

@Mapper
public interface ScheduleMapper {

    void addSchedule(ScheduleDTO scheduleDTO);
}
