package com.Reboot.Minty.trade.repository;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.ScheduleDay;
import com.Reboot.Minty.trade.entity.ScheduleDuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.Optional;

@Repository
public interface ScheduleDayRepository extends JpaRepository<ScheduleDay, Long> {

    ScheduleDay findByUserId(User user);

}
