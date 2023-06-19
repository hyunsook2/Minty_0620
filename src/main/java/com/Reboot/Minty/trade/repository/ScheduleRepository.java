package com.Reboot.Minty.trade.repository;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.Schedule;
import com.Reboot.Minty.trade.entity.ScheduleDuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.Set;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Schedule findByUserId(User user);

    boolean existsByUserIdAndHopeArea(User user, String hopeArea);

    boolean existsByUserIdAndHopeDay(User user, DayOfWeek hopeDay);

    boolean existsByUserIdAndScheduleDuration(User user, ScheduleDuration scheduleDuration);

    boolean existsByUserIdAndIntroduction(User user, String introduction);
}
