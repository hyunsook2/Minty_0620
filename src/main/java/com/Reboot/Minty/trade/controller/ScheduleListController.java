package com.Reboot.Minty.trade.controller;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.trade.entity.Schedule;
import com.Reboot.Minty.trade.repository.ScheduleRepository;
import com.Reboot.Minty.trade.service.ScheduleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ScheduleListController {
    private final UserRepository userRepository;

    private final ScheduleRepository scheduleRepository;

    private final ScheduleService scheduleService;

    public ScheduleListController(UserRepository userRepository, ScheduleRepository scheduleRepository, ScheduleService scheduleService) {
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/scheduleList")
    public String scheduleList(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        Schedule schedule = scheduleRepository.findByUserId(user);

        boolean checkDay = false;
        boolean checkArea = false;
        boolean checkDuration = false;
        boolean checkIntroduction = false;

        if (schedule != null) {
            if (schedule.getHopeDay() != null) {
                checkDay = scheduleService.checkDay(schedule, schedule.getHopeDay());
            }
            if (schedule.getHopeArea() != null) {
                checkArea = scheduleService.checkArea(schedule, schedule.getHopeArea());
            }
            if (schedule.getScheduleDuration() != null) {
                checkDuration = scheduleService.checkDuration(schedule, schedule.getScheduleDuration());
            }
            if (schedule.getIntroduction() != null) {
                checkIntroduction = scheduleService.checkIntroduction(schedule, schedule.getIntroduction());
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("schedule", schedule);
        model.addAttribute("checkDay", checkDay);
        model.addAttribute("checkArea", checkArea);
        model.addAttribute("checkDuration", checkDuration);
        model.addAttribute("checkIntroduction", checkIntroduction);

        return "trade/scheduleList";
    }

}
