package com.Reboot.Minty.member.controller;


import com.Reboot.Minty.event.dto.AttendanceDto;
import com.Reboot.Minty.event.entity.Attendance;
import com.Reboot.Minty.event.service.AttendanceService;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.review.entity.Review;
import com.Reboot.Minty.review.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class MyPageController {

    private  final AttendanceService attendanceService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ReviewService reviewService;

    @Autowired
    public MyPageController(AttendanceService attendanceService, UserRepository userRepository, UserService userService, ReviewService reviewService) {
        this.attendanceService = attendanceService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping("mypage")
    public String showMyPage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        System.out.println(user.getNickName());

        if (user != null) {
            model.addAttribute("user", user);
        } else {
            model.addAttribute("errorMessage", "회원 정보를 찾을 수 없습니다.");
        }

        List<Review> myReviews = reviewService.getReviewsByWriterIdOrderByWriteTimeDesc(user);
        model.addAttribute("myReviews", myReviews);

        List<Review> receivedReviews = reviewService.getReceivedReviewsByReceiverIdOrderByWriteTimeDesc(user);
        model.addAttribute("receivedReviews", receivedReviews);

        double averageRating = reviewService.calculateAverageRating(receivedReviews);
        model.addAttribute("averageRating", averageRating);

        return "member/myPage";
    }
    @GetMapping("/event")
    public String showEventForm(){
        return "event/event";
    }

}