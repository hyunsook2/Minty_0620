package com.Reboot.Minty.member.controller;

import com.Reboot.Minty.event.service.AttendanceService;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.review.entity.Review;
import com.Reboot.Minty.review.service.ReviewService;
import com.Reboot.Minty.trade.service.TradeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class UserShopController {
    private final ReviewService reviewService;
    private final UserRepository userRepository;
    private final TradeService tradeService;
    private final UserService userService;
    private final AttendanceService attendanceService;

    @Autowired
    public UserShopController(ReviewService reviewService, UserRepository userRepository, TradeService tradeService, UserService userService, AttendanceService attendanceService) {
        this.reviewService = reviewService;
        this.userRepository = userRepository;
        this.tradeService = tradeService;
        this.userService = userService;
        this.attendanceService = attendanceService;
    }

    @GetMapping(value = {"usershop", "usershop/{userId}"})
    public String usershop(HttpServletRequest request, Model model, @PathVariable(required = false) Long userId) {
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");

        User currentUser = userService.getUserInfo(userEmail);
        model.addAttribute("user", currentUser);

        Long ownerId;

        if (userId != null) {
            User otherUser = userService.getUserById(userId);
            if (otherUser != null) {
                model.addAttribute("user", otherUser);
                List<Review> receivedReviews = reviewService.getReceivedReviewsByReceiverIdOrderByWriteTimeDesc(otherUser);
                model.addAttribute("receivedReviews", receivedReviews);
                double averageRating = reviewService.calculateAverageRating(receivedReviews);
                model.addAttribute("averageRating", averageRating);

                ownerId = otherUser.getId(); // 상점 주인의 ID 설정
            } else {
                model.addAttribute("errorMessage", "회원 정보를 찾을 수 없습니다.");
                return "member/userShop";
            }
        } else {
            List<Review> receivedReviews = reviewService.getReceivedReviewsByReceiverIdOrderByWriteTimeDesc(currentUser);
            model.addAttribute("receivedReviews", receivedReviews);
            double averageRating = reviewService.calculateAverageRating(receivedReviews);
            model.addAttribute("averageRating", averageRating);

            ownerId = currentUser.getId(); // 상점 주인의 ID 설정
        }

        int tradeCount = tradeService.getTradeCountForStoreOwner(ownerId);
        model.addAttribute("tradeCount", tradeCount);

        int totalCount = attendanceService.getAttendanceTotalCount(ownerId);
        model.addAttribute("totalCount", totalCount);

        return "member/userShop";
    }
}