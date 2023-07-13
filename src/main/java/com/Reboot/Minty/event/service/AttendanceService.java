package com.Reboot.Minty.event.service;

import com.Reboot.Minty.event.entity.Attendance;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.event.repository.AttendanceRepository;
import com.Reboot.Minty.member.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    public Attendance getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    @Transactional
    public Attendance saveAttendance(Long userId, LocalDate date) {
        Attendance attendance = new Attendance();
        attendance.setUserId(userId);
        attendance.setDate(date);
        attendance.setPoint(100);

        Attendance savedAttendance = attendanceRepository.save(attendance);

        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            int currentPoint = user.getPoint();
            int attendancePoint = savedAttendance.getPoint();
            int updatedPoint = currentPoint + attendancePoint;
            user.setPoint(updatedPoint);
            userRepository.save(user);
        }

        return savedAttendance;
    }

    public Attendance getAttendanceByDateAndUserId(LocalDate date, Long userId) {
        return attendanceRepository.findByDateAndUserId(date, userId);
    }

    public int getAttendanceTotalCount(Long ownerId) {
        return attendanceRepository.getAttendanceTotalCount(ownerId);
    }

}