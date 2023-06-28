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
    public String usershop(HttpServletRequest request, Model model, Optional<Long> otherUserId) {
        if(!otherUserId.isPresent()) {
            HttpSession session = request.getSession();
            String userEmail = (String) session.getAttribute("userEmail");
            User user = userService.getUserInfo(userEmail);
            Long userId = user.getId();

            List<Review> receivedReviews = reviewService.getReceivedReviewsByReceiverIdOrderByWriteTimeDesc(user);

            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("receivedReviews", receivedReviews);
            } else {
                model.addAttribute("errorMessage", "회원 정보를 찾을 수 없습니다.");
            }

            return "member/userShop";

        }else{
            User user = userService.getUserById(otherUserId.orElseThrow(EntityExistsException::new));
            List<Review> receivedReviews = reviewService.getReceivedReviewsByReceiverIdOrderByWriteTimeDesc(user);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("receivedReviews", receivedReviews);
            } else {
                model.addAttribute("errorMessage", "회원 정보를 찾을 수 없습니다.");
            }

            return "member/userShop";
        }
    }

}