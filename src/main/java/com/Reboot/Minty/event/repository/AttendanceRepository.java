package com.Reboot.Minty.event.repository;

import com.Reboot.Minty.event.entity.Attendance;
import com.Reboot.Minty.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Attendance findByDate(LocalDate date);
    Attendance findByDateAndUserId(LocalDate date, Long userId);
}
