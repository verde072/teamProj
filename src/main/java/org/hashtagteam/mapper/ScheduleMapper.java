package org.hashtagteam.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.hashtagteam.dto.ScheduleDTO;
import org.hashtagteam.model.Schedule;

import java.util.List;

@Mapper
public interface ScheduleMapper {
    @Select("SELECT schedule_id, start_time, end_time, title, content, color " +
            "FROM schedule " +
            "WHERE user_id = #{userId}")
    List<ScheduleDTO> getAllSchedules(String userId);

    @Insert("INSERT INTO schedule (schedule_id, user_id, start_time, end_time, title, content, color) " +
            "VALUES (#{scheduleId}, #{userId}, #{startTime}, #{endTime}, #{title}, #{content}, #{color})")
    void addSchedule(Schedule schedule);

    @Delete("DELETE FROM schedule WHERE user_id = #{userId} AND start_time = #{startTime} AND end_time = #{endTime}")
    int delete(Schedule schedule);
}
