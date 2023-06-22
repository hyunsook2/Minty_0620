package com.Reboot.Minty.trade.controller;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.trade.entity.Schedule;
import com.Reboot.Minty.trade.entity.ScheduleDay;
import com.Reboot.Minty.trade.entity.ScheduleDuration;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.trade.repository.ScheduleRepository;
import com.Reboot.Minty.trade.repository.TradeRepository;
import com.Reboot.Minty.trade.service.ScheduleListService;
import com.Reboot.Minty.trade.service.ScheduleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ScheduleListController {
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleService scheduleService;

    private final ScheduleListService scheduleListService;

    private final TradeRepository tradeRepository;

    public ScheduleListController(UserRepository userRepository, ScheduleRepository scheduleRepository, ScheduleService scheduleService, ScheduleListService scheduleListService, TradeRepository tradeRepository) {
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleService = scheduleService;
        this.scheduleListService = scheduleListService;
        this.tradeRepository = tradeRepository;
    }

    @GetMapping("/scheduleList")
    public String scheduleList(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        Schedule schedule = scheduleService.getSchedule(user);
        ScheduleDay scheduleDay = scheduleListService.getScheduleDay(user);
        List<ScheduleDuration> scheduleDuration = scheduleListService.getScheduleDuration(user);
        List<Trade> userTrades = tradeRepository.findByBuyerId_Id(userId);
        userTrades.addAll(tradeRepository.findBySellerId_Id(userId));


        boolean checkDay = false;
        boolean checkArea = false;
        boolean checkDuration = true;
        boolean checkIntroduction = false;

        if (schedule != null) {
            checkArea = scheduleService.checkArea(schedule, schedule.getHopeArea());
            checkIntroduction = scheduleService.checkIntroduction(schedule);
        }

        checkDay = scheduleService.checkDay(user);
        checkDuration = scheduleService.checkDuration(user);

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalDateTime currentDateTime = LocalDateTime.of(currentDate, currentTime);
        userTrades = userTrades.stream()
                .filter(trade -> {
                    LocalDate tradeDate = trade.getTradeDate();
                    LocalTime tradeTime = trade.getTradeTime();
                    if (tradeDate == null || tradeTime == null) {
                        return false; // 거래일 또는 거래시간이 null인 경우에는 false를 반환하여 조회 제한
                    }
                    LocalDateTime tradeDateTime = LocalDateTime.of(tradeDate, tradeTime);
                    return tradeDateTime.isAfter(currentDateTime);
                })
                .collect(Collectors.toList());


        model.addAttribute("userTrades", userTrades);

        model.addAttribute("user", user);
        model.addAttribute("schedule", schedule);
        model.addAttribute("checkDay", checkDay);
        model.addAttribute("checkArea", checkArea);
        model.addAttribute("checkDuration", checkDuration);
        model.addAttribute("checkIntroduction", checkIntroduction);

        model.addAttribute("scheduleDay", scheduleDay);  // Schedule 엔티티의 day 필드
        model.addAttribute("scheduleDuration", scheduleDuration);  // Schedule 엔티티의 duration 필드


        return "trade/scheduleList";
    }

}
