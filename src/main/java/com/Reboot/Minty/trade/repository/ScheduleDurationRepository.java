package com.Reboot.Minty.trade.repository;


import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.Schedule;
import com.Reboot.Minty.trade.entity.ScheduleDuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleDurationRepository extends JpaRepository<ScheduleDuration, Long> {

    List<ScheduleDuration> findByUserId(User user);

}

