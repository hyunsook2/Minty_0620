package com.Reboot.Minty.trade.controller;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.trade.entity.Schedule;
import com.Reboot.Minty.trade.entity.ScheduleDay;
import com.Reboot.Minty.trade.entity.ScheduleDuration;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.trade.repository.ScheduleDayRepository;
import com.Reboot.Minty.trade.repository.ScheduleDurationRepository;
import com.Reboot.Minty.trade.repository.ScheduleRepository;
import com.Reboot.Minty.trade.repository.TradeRepository;
import com.Reboot.Minty.trade.service.ScheduleListService;
import com.Reboot.Minty.trade.service.ScheduleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    private final ScheduleDayRepository scheduleDayRepository;

    private final ScheduleDurationRepository scheduleDurationRepository;

    public ScheduleListController(UserRepository userRepository, ScheduleRepository scheduleRepository, ScheduleService scheduleService, ScheduleListService scheduleListService, TradeRepository tradeRepository, ScheduleDayRepository scheduleDayRepository, ScheduleDurationRepository scheduleDurationRepository) {
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleService = scheduleService;
        this.scheduleListService = scheduleListService;
        this.tradeRepository = tradeRepository;
        this.scheduleDayRepository = scheduleDayRepository;
        this.scheduleDurationRepository = scheduleDurationRepository;
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

    @PostMapping("/scheduleRegist")
    public String scheduleRegist(@RequestParam(name = "scheduleDay") List<Integer> days ,
                                 @RequestParam(name = "introduction") String introduction,
                                 @RequestParam(name = "startTime[]") List<String> startTimeStrings,
                                 @RequestParam(name = "endTime[]") List<String> endTimeStrings,
                                 HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        // scheduleDay 엔티티 생성
        ScheduleDay scheduleDay = new ScheduleDay();

        scheduleDay.setUserId(user);
        scheduleDay.setSunday(days.get(0));
        scheduleDay.setMonday(days.get(1));
        scheduleDay.setTuesday(days.get(2));
        scheduleDay.setWednesday(days.get(3));
        scheduleDay.setThursday(days.get(4));
        scheduleDay.setFriday(days.get(5));
        scheduleDay.setSaturday(days.get(6));

        scheduleListService.saveScheduleDay(scheduleDay);

        // ScheduleDuration 엔티티 생성
        for (int i = 0; i < startTimeStrings.size(); i++) {
            String startTimeString = startTimeStrings.get(i);
            String endTimeString = endTimeStrings.get(i);

            ScheduleDuration scheduleDuration = new ScheduleDuration();

            scheduleDuration.setUserId(user);
            scheduleDuration.setStartTime(LocalTime.parse(startTimeString));
            scheduleDuration.setEndTime(LocalTime.parse(endTimeString));

            scheduleListService.saveScheduleDuration(scheduleDuration);
        }

        if (introduction != null) {
            Schedule schedule = new Schedule();
            schedule.setUser(user);
            schedule.setIntroduction(introduction);

            scheduleListService.saveSchedule(schedule);
        }

        // 저장 또는 업데이트 후에 리다이렉트할 페이지로 이동
        return "redirect:/scheduleList";
    }


    @PostMapping("/updateDay")
    public String updateDay(@RequestParam(name = "editScheduleDay") List<Integer> days,
                            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

            ScheduleDay scheduleDay = scheduleDayRepository.findByUserId(user);
            if (scheduleDay != null) {
                scheduleDay.setSunday(days.get(0));
                scheduleDay.setMonday(days.get(1));
                scheduleDay.setTuesday(days.get(2));
                scheduleDay.setWednesday(days.get(3));
                scheduleDay.setThursday(days.get(4));
                scheduleDay.setFriday(days.get(5));
                scheduleDay.setSaturday(days.get(6));

                scheduleListService.saveScheduleDay(scheduleDay);
            }
            // 저장 또는 업데이트 후에 리다이렉트할 페이지로 이동
            return "redirect:/scheduleList";
        } catch (Exception e) {
            // 오류 처리 로직을 추가하세요
            return "redirect:/scheduleList";
        }
    }

    @PostMapping("/updateDuration")
    public String updateDuration(@RequestParam(name = "editStartTime[]") List<String> editStartTimeStrings,
                                 @RequestParam(name = "editEndTime[]") List<String> editEndTimeStrings,
                                 @RequestParam(name = "editDurationId[]") List<String> editDurationIdStrings,
                                 HttpSession session) {

        System.out.println(editStartTimeStrings);
        System.out.println(editEndTimeStrings);
        System.out.println(editDurationIdStrings);

        try {
            Long userId = (Long) session.getAttribute("userId");
            User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

            int maxLength = Math.max(editStartTimeStrings.size(), editEndTimeStrings.size());

            for (int i = 0; i < maxLength; i++) {
                if (i < editDurationIdStrings.size()) {
                    // 기존 ScheduleDuration 엔티티 수정
                    String editDurationIdStr = editDurationIdStrings.get(i);
                    ScheduleDuration scheduleDuration = scheduleDurationRepository.findById(Long.parseLong(editDurationIdStr))
                            .orElseThrow(EntityNotFoundException::new);

                    String startTimeString = editStartTimeStrings.get(i);
                    String endTimeString = editEndTimeStrings.get(i);

                    scheduleDuration.setStartTime(LocalTime.parse(startTimeString));
                    scheduleDuration.setEndTime(LocalTime.parse(endTimeString));

                    scheduleListService.saveScheduleDuration(scheduleDuration);
                } else {
                    // 새로운 ScheduleDuration 엔티티 생성 및 저장
                    String startTimeString = editStartTimeStrings.get(i);
                    String endTimeString = editEndTimeStrings.get(i);

                    ScheduleDuration scheduleDuration = new ScheduleDuration();
                    scheduleDuration.setUserId(user);
                    scheduleDuration.setStartTime(LocalTime.parse(startTimeString));
                    scheduleDuration.setEndTime(LocalTime.parse(endTimeString));

                    scheduleListService.saveScheduleDuration(scheduleDuration);
                }
            }
            // 저장 또는 업데이트 후에 리다이렉트할 페이지로 이동
            return "redirect:/scheduleList";
        } catch (Exception e) {
            // 오류 처리 로직을 추가하세요
            return "redirect:/scheduleList";
        }
    }

    @PostMapping("/updateIntroduction")
    public String updateIntroduction(@RequestParam(name = "editIntroduction") String editIntroduction,
                            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

            if (editIntroduction != null) {
                Schedule schedule = scheduleRepository.findByUser(user);
                schedule.setIntroduction(editIntroduction);

                scheduleListService.saveSchedule(schedule);
            }

            // 저장 또는 업데이트 후에 리다이렉트할 페이지로 이동
            return "redirect:/scheduleList";
        } catch (Exception e) {
            // 오류 처리 로직을 추가하세요
            return "redirect:/scheduleList";
        }
    }
}


