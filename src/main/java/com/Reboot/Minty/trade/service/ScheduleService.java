package com.Reboot.Minty.trade.service;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.Schedule;
import com.Reboot.Minty.trade.entity.ScheduleDay;
import com.Reboot.Minty.trade.entity.ScheduleDuration;
import com.Reboot.Minty.trade.repository.ScheduleDayRepository;
import com.Reboot.Minty.trade.repository.ScheduleDurationRepository;
import com.Reboot.Minty.trade.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    private final ScheduleDayRepository scheduleDayRepository;

    private final ScheduleDurationRepository scheduleDurationRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, ScheduleDayRepository scheduleDayRepository, ScheduleDurationRepository scheduleDurationRepository) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleDayRepository = scheduleDayRepository;
        this.scheduleDurationRepository = scheduleDurationRepository;
    }

    //id값에 맞는 Schedule 정보 가져오기
    public Schedule getSchedule(User user) {
        Schedule schedule = scheduleRepository.findByUser(user);
        return schedule;
    }

    // 해당 유저의 Schedule 정보가 있는지 확인

    public boolean checkDay(User user) {
        Optional<ScheduleDay> scheduleDay = Optional.ofNullable(scheduleDayRepository.findByUserId(user));
        return scheduleDay.isPresent();
    }

    public boolean checkDuration(User user) {
        List<ScheduleDuration> foundScheduleDuration = scheduleDurationRepository.findByUserId(user);
        if(foundScheduleDuration.isEmpty()){
            return false;
        }else return true;
    }

    public boolean checkIntroduction(Schedule schedule) {
            boolean flag = true;
            if(schedule.getIntroduction()==null){
                flag = false;
            }
            return flag;
    }


}
