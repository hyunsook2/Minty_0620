package com.Reboot.Minty.trade.service;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.Schedule;
import com.Reboot.Minty.trade.entity.ScheduleDuration;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.trade.repository.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.Set;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    //id값에 맞는 Schedule 정보 가져오기
    public Schedule getSchedule(User userId) {
        Schedule schedule = scheduleRepository.findByUserId(userId);
        return schedule;
    }

    //해당 유저의 Schedule 정보가 있는지 확인
    public boolean checkArea(Schedule schedule, String hopeArea) {
        return scheduleRepository.existsByUserIdAndHopeArea(schedule.getUserId(), hopeArea);
    }

    public boolean checkDay(Schedule schedule, DayOfWeek hopeDay) {
        return scheduleRepository.existsByUserIdAndHopeDay(schedule.getUserId(), hopeDay);
    }

    public boolean checkDuration(Schedule schedule, ScheduleDuration scheduleDuration) {
        return scheduleRepository.existsByUserIdAndScheduleDuration(schedule.getUserId(), scheduleDuration);
    }

    public boolean checkIntroduction(Schedule schedule, String introduction) {
        return scheduleRepository.existsByUserIdAndIntroduction(schedule.getUserId(), introduction);
    }




}
