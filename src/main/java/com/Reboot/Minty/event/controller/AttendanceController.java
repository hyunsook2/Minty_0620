package com.Reboot.Minty.event.controller;

import com.Reboot.Minty.event.dto.AttendanceDto;
import com.Reboot.Minty.event.entity.Attendance;
import com.Reboot.Minty.event.service.AttendanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class AttendanceController {
    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public List<Attendance> getAllAttendances() {
        return attendanceService.getAllAttendances();
    }

    @GetMapping("/attendance/{date}")
    public Attendance getAttendanceByDate(@PathVariable("date") String dateString) {
        if (dateString.equals("favicon.ico")) {
            // favicon.ico 요청은 무시하고 null을 반환하거나 적절한 예외 처리를 수행합니다.
            // 예: throw new IllegalArgumentException("Invalid date");
            return null;
        }
        LocalDate date = LocalDate.parse(dateString);
        return attendanceService.getAttendanceByDate(date);
    }

    @GetMapping("/attendance")
    public String attendanceForm(Model model, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId"); // 세션에서 로그인한 사용자의 아이디 가져오기
            model.addAttribute("userId", userId);
            model.addAttribute("message", "출석체크ㅎㅇ");
            return "event/attendance";
        } catch (Exception e) {
            model.addAttribute("error", "요청을 처리하는 중에 오류가 발생했습니다.");
            e.printStackTrace();
            return "redirect:/event";
        }
    }

    @PostMapping("/attendance/save")
    @ResponseBody
    public Attendance saveAttendance(@RequestBody AttendanceDto attendanceDto, HttpSession session) {
        try {
            LocalDate date = attendanceDto.getDate();
            Long userId = (Long) session.getAttribute("userId");

            Attendance existingAttendance = attendanceService.getAttendanceByDateAndUserId(date, userId);
            if (existingAttendance != null) {
                throw new RuntimeException("해당 날짜에 이미 출석 정보가 있습니다.");
            }

            Attendance savedAttendance = attendanceService.saveAttendance(userId, date);
            return savedAttendance;
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("출석 정보를 저장하는 중에 오류가 발생했습니다.");
        }
    }

    @GetMapping("/attendance/checkStatus/{date}")
    @ResponseBody
    public boolean getCheckStatusByDate(@PathVariable("date") String dateString, HttpSession session) {
        try {
            if (dateString.equals("favicon.ico")) {
                return false;
            }
            LocalDate date = LocalDate.parse(dateString);
            Long userId = (Long) session.getAttribute("userId");
            Attendance attendance = attendanceService.getAttendanceByDateAndUserId(date, userId);
            return attendance != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("출석 정보를 확인하는 중에 오류가 발생했습니다.");
        }
    }

}