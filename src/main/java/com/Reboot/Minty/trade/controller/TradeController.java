package com.Reboot.Minty.trade.controller;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.review.dto.ReviewDto;
import com.Reboot.Minty.review.entity.Review;
import com.Reboot.Minty.review.repository.ReviewRepository;
import com.Reboot.Minty.review.service.ReviewService;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.trade.service.TradeService;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
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

import java.util.List;

@Controller
public class TradeController {
    private final TradeService tradeService;
    private final TradeBoardService tradeBoardService;

    private final UserService userService;

    private final ReviewService reviewService;

    @Autowired
    public TradeController(TradeService tradeService, TradeBoardService tradeBoardService, UserService userService, ReviewService reviewService) {
        this.tradeService = tradeService;
        this.tradeBoardService = tradeBoardService;
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping("/tradeList")
    public String tradeList(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");
        List<Trade> trades = tradeService.getTradeList(userId);
        List<User> users = tradeService.getTradeUsers(trades, userId);

        model.addAttribute("trades", trades);
        model.addAttribute("users", users);
        return "trade/tradeList";
    }

    @GetMapping(value = "/trade/{tradeId}")
    public String trade(@PathVariable(value = "tradeId") Long tradeId, Model model, HttpServletRequest request)  {
        HttpSession session = request.getSession();
        Long userId = (Long)session.getAttribute("userId");
        User writerId = userService.getUserInfoById(userId);
        Trade trade = tradeService.getTradeDetail(tradeId);
        String role = tradeService.getRoleForTrade(tradeId, userId);
        User buyer= userService.getUserInfoById(trade.getBuyerId().getId());
        User seller= userService.getUserInfoById(trade.getSellerId().getId());
        Review review = reviewService.getReviewByTradeIdAndWriterId(trade,writerId);
        boolean isExistReview = reviewService.existsByIdAndWriterId(trade,writerId);
        model.addAttribute("trade", trade);
        model.addAttribute("role",role);
        model.addAttribute("buyer",buyer);
        model.addAttribute("seller",seller);
        model.addAttribute("review",review);
        model.addAttribute("isExistReview",isExistReview);

        return "trade/trade";
    }

    @PostMapping("/api/purchasingReq")
    @ResponseBody
    public ResponseEntity<?> purchasingReq(@RequestBody TradeBoard tradeBoard, HttpSession session) {
        try {
            User buyer = userService.getUserInfoById((Long) session.getAttribute("userId"));
            User seller = userService.getUserInfoById(tradeBoard.getUser().getId());
            Trade trade = tradeService.save(tradeBoard, buyer, seller);
            return ResponseEntity.ok("/trade/" + trade.getId());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("/trade/"+e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @GetMapping(value = "tradeDetail/{tradeId}")
    public String tradtradeDetaile(@PathVariable(value = "tradeId") Long tradeId, Model model, HttpServletRequest request)  {


        return "/";
    }

    @PostMapping("/updateStatus")
    @Transactional
    public String updateStatus(@RequestParam("tradeId") Long tradeId, @RequestParam("statusIndex") int statusIndex) {
        try {
            tradeService.updateStatus(tradeId, statusIndex);
            // 현재 페이지를 리로드하는 JavaScript 코드를 반환
            return "redirect:/trade/" + tradeId;
        } catch (Exception e) {
            e.printStackTrace(); // 에러 정보를 로그에 출력하거나 원하는 방식으로 처리할 수 있습니다.
            // 오류 페이지로 리다이렉트하거나 오류 메시지를 표시하는 등의 처리를 수행합니다.
            return "error";
        }
    }

    @PostMapping("/updateMode")
    @Transactional
    public String updateMode(@RequestParam("tradeId") Long tradeId, @RequestParam("modeIndex") int modeIndex) {
        try {
            tradeService.updateMode(tradeId, modeIndex);
            // 현재 페이지를 리로드하는 JavaScript 코드를 반환
            return "redirect:/trade/" + tradeId;
        } catch (Exception e) {
            e.printStackTrace(); // 에러 정보를 로그에 출력하거나 원하는 방식으로 처리할 수 있습니다.
            // 오류 페이지로 리다이렉트하거나 오류 메시지를 표시하는 등의 처리를 수행합니다.
            return "error";
        }
    }


}
