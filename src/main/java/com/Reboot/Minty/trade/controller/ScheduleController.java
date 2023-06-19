package com.Reboot.Minty.trade.controller;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.trade.entity.Schedule;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.trade.repository.ScheduleRepository;
import com.Reboot.Minty.trade.service.ScheduleService;
import com.Reboot.Minty.trade.service.TradeService;
import com.Reboot.Minty.tradeBoard.service.TradeBoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class ScheduleController {

    private final TradeService tradeService;

    private final TradeBoardService tradeBoardService;
    private final UserService userService;
    private final ScheduleRepository scheduleRepository;

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(TradeService tradeService, TradeBoardService tradeBoardService, UserService userService, ScheduleRepository scheduleRepository, ScheduleService scheduleService) {
        this.tradeService = tradeService;
        this.tradeBoardService = tradeBoardService;
        this.userService = userService;
        this.scheduleRepository = scheduleRepository;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/schedule/{tradeId}")
    public String schedule(@PathVariable(value = "tradeId") Long tradeId,Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");
        Trade trade = tradeService.getTradeDetail(tradeId);
        String role = tradeService.getRoleForTrade(tradeId, userId);
        User buyer= userService.getUserInfoById(trade.getBuyerId().getId());
        User seller= userService.getUserInfoById(trade.getSellerId().getId());
        Schedule buyerSchedule = scheduleRepository.findByUserId(buyer);
        Schedule sellerSchedule = scheduleRepository.findByUserId(seller);

        boolean buyerCheckDay = false;
        boolean buyerCheckArea = false;
        boolean buyerCheckDuration = false;
        boolean buyerCheckIntroduction = false;
        boolean sellerCheckDay = false;
        boolean sellerCheckArea = false;
        boolean sellerCheckDuration = false;
        boolean sellerCheckIntroduction = false;

        if (buyerSchedule != null) {
            if (buyerSchedule.getHopeDay() != null) {
                buyerCheckDay = scheduleService.checkDay(buyerSchedule, buyerSchedule.getHopeDay());
            }
            if (buyerSchedule.getHopeArea() != null) {
                buyerCheckArea = scheduleService.checkArea(buyerSchedule, buyerSchedule.getHopeArea());
            }
            if (buyerSchedule.getScheduleDuration() != null) {
                buyerCheckDuration = scheduleService.checkDuration(buyerSchedule, buyerSchedule.getScheduleDuration());
            }
            if (buyerSchedule.getIntroduction() != null) {
                buyerCheckIntroduction = scheduleService.checkIntroduction(buyerSchedule, buyerSchedule.getIntroduction());
            }
        }

        if (sellerSchedule != null) {
            if (sellerSchedule.getHopeDay() != null) {
                sellerCheckDay = scheduleService.checkDay(sellerSchedule, sellerSchedule.getHopeDay());
            }
            if (sellerSchedule.getHopeArea() != null) {
                sellerCheckArea = scheduleService.checkArea(sellerSchedule, sellerSchedule.getHopeArea());
            }
            if (sellerSchedule.getScheduleDuration() != null) {
                sellerCheckDuration = scheduleService.checkDuration(sellerSchedule, sellerSchedule.getScheduleDuration());
            }
            if (sellerSchedule.getIntroduction() != null) {
                sellerCheckIntroduction = scheduleService.checkIntroduction(sellerSchedule, sellerSchedule.getIntroduction());
            }
        }

        model.addAttribute("sellerSchedule", sellerSchedule);
        model.addAttribute("buyerSchedule", buyerSchedule);
        model.addAttribute("sellerCheckDay", sellerCheckDay);
        model.addAttribute("sellerCheckArea", sellerCheckArea);
        model.addAttribute("sellerCheckDuration", sellerCheckDuration);
        model.addAttribute("sellerCheckIntroduction", sellerCheckIntroduction);
        model.addAttribute("buyerCheckDay", buyerCheckDay);
        model.addAttribute("buyerCheckArea", buyerCheckArea);
        model.addAttribute("buyerCheckDuration", buyerCheckDuration);
        model.addAttribute("buyerCheckIntroduction", buyerCheckIntroduction);

        model.addAttribute("trade", trade);
        model.addAttribute("role",role);
        model.addAttribute("buyer",buyer);
        model.addAttribute("seller",seller);
        model.addAttribute("tradeId", tradeId);
        model.addAttribute("buyerSchedule",buyerSchedule);
        model.addAttribute("sellerSchedule",sellerSchedule);
        return "trade/schedule";
    }

    @PostMapping("/updateTradeSchedule")
    @Transactional
    public ResponseEntity TradeSchedule(@RequestParam("tradeId") Long tradeId, @RequestParam("tradeDate") LocalDate tradeDate, @RequestParam("tradeTime") LocalTime tradeTime) {
        try {
            tradeService.updateTradeSchedule(tradeId, tradeDate, tradeTime);
            tradeService.updateStatus(tradeId, 2);

            // 현재 페이지를 리로드하는 JavaScript 코드를 반환
            return ResponseEntity.ok("/trade/" + tradeId);
        } catch (Exception e) {
            e.printStackTrace(); // 에러 정보를 로그에 출력하거나 원하는 방식으로 처리할 수 있습니다.
            // 오류 페이지로 리다이렉트하거나 오류 메시지를 표시하는 등의 처리를 수행합니다.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("/schedule/"+tradeId);
        }
    }


}