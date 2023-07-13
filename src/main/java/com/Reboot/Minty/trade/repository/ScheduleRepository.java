package com.Reboot.Minty.trade.repository;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Schedule findByUser(User user);


    boolean existsByUserIdAndIntroduction(User user, String introduction);
}
