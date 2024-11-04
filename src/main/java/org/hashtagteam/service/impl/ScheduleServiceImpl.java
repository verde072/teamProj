package org.hashtagteam.service.impl;

import org.hashtagteam.dto.ScheduleDTO;
import org.hashtagteam.mapper.ScheduleMapper;
import org.hashtagteam.service.ScheduleService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final ModelMapper modelMapper;

    // 생성자
    public ScheduleServiceImpl(ScheduleMapper scheduleMapper, ModelMapper modelMapper) {
        this.scheduleMapper = scheduleMapper;
        this.modelMapper = modelMapper;
    }

    //TODO 스케줄 등록 저장
    @Override
    public ScheduleDTO addSchedule(ScheduleDTO scheduleDTO) {
        scheduleMapper.addSchedule(scheduleDTO);

        return null;
    }

}