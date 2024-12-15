package org.hashtagteam.service.impl;

import org.hashtagteam.dto.ScheduleDTO;
import org.hashtagteam.mapper.ScheduleMapper;
import org.hashtagteam.model.Schedule;
import org.hashtagteam.service.ScheduleService;
import org.hashtagteam.utils.IdGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final ModelMapper modelMapper;

    // 생성자
    public ScheduleServiceImpl(ScheduleMapper scheduleMapper, ModelMapper modelMapper) {
        this.scheduleMapper = scheduleMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ScheduleDTO> getAllSchedules(String userId) {
        return scheduleMapper.getAllSchedules(userId);
    }

    @Override
    public void addSchedule(ScheduleDTO scheduleDTO) {
        try {
            scheduleDTO.setScheduleId(IdGenerator.generateId("S"));
            Schedule schedule = modelMapper.map(scheduleDTO, Schedule.class);
            scheduleMapper.addSchedule(schedule);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while adding schedule: " + e);
        }
    }

    @Override
    public boolean delete(ScheduleDTO scheduleDTO) {
        Schedule schedule = modelMapper.map(scheduleDTO, Schedule.class);
        int deletedRows = scheduleMapper.delete(schedule);
        return deletedRows > 0; // 삭제된 행이 1개 이상이면 true 반환
    }
}