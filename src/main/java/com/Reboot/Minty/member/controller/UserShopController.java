package com.Reboot.Minty.member.controller;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.review.entity.Review;
import com.Reboot.Minty.review.service.ReviewService;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class UserShopController {
    @Autowired
    private ReviewService reviewService;
    private final UserRepository userRepository;
    public UserShopController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Autowired
    private UserService userService;

    @GetMapping(value = {"usershop", "usershop/{userId}"})
    public String usershop(HttpServletRequest request, Model model, @PathVariable(required = false) Long userId) {
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");

        User currentUser = userService.getUserInfo(userEmail);
        model.addAttribute("user", currentUser);

        if (userId != null) {
            User otherUser = userService.getUserById(userId);
            if (otherUser != null) {
                model.addAttribute("user", otherUser);
                List<Review> receivedReviews = reviewService.getReceivedReviewsByReceiverIdOrderByWriteTimeDesc(otherUser);
                model.addAttribute("receivedReviews", receivedReviews);
                double averageRating = reviewService.calculateAverageRating(receivedReviews);
                model.addAttribute("averageRating", averageRating);
            } else {
                model.addAttribute("errorMessage", "회원 정보를 찾을 수 없습니다.");
            }
        } else {
            List<Review> receivedReviews = reviewService.getReceivedReviewsByReceiverIdOrderByWriteTimeDesc(currentUser);
            model.addAttribute("receivedReviews", receivedReviews);
            double averageRating = reviewService.calculateAverageRating(receivedReviews);
            model.addAttribute("averageRating", averageRating);
        }

        return "member/userShop";
    }

}