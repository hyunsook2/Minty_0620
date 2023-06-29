package com.Reboot.Minty.trade.service;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.Schedule;
import com.Reboot.Minty.trade.entity.ScheduleDay;
import com.Reboot.Minty.trade.entity.ScheduleDuration;
import com.Reboot.Minty.trade.repository.ScheduleDayRepository;
import com.Reboot.Minty.trade.repository.ScheduleDurationRepository;
import com.Reboot.Minty.trade.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleListService {
    private final ScheduleRepository scheduleRepository;

    private final ScheduleDayRepository scheduleDayRepository;

    private final ScheduleDurationRepository scheduleDurationRepository;

    public ScheduleListService(ScheduleRepository scheduleRepository, ScheduleDayRepository scheduleDayRepository, ScheduleDurationRepository scheduleDurationRepository) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleDayRepository = scheduleDayRepository;
        this.scheduleDurationRepository = scheduleDurationRepository;
    }

    public ScheduleDay getScheduleDay(User user) {
        ScheduleDay scheduleday = scheduleDayRepository.findByUserId(user);
        return scheduleday;
    }

    public List<ScheduleDuration> getScheduleDuration(User user) {
        List<ScheduleDuration> scheduleDuration = scheduleDurationRepository.findByUserId(user);
        return scheduleDuration;
    }

    public void saveSchedule(Schedule schedule) {
        scheduleRepository.save(schedule);
    }

    public void saveScheduleDay(ScheduleDay scheduleDay) {
        scheduleDayRepository.save(scheduleDay);
    }

    public void saveScheduleDuration(ScheduleDuration scheduleDuration) {
        scheduleDurationRepository.save(scheduleDuration);
    }

}
