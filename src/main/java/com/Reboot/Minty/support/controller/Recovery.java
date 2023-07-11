package com.Reboot.Minty.support.controller;

import com.Reboot.Minty.member.constant.Role;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.support.service.EmailAuthService;
import com.Reboot.Minty.support.service.RecoveryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Recovery {
    @Autowired
    EmailAuthService emailAuthService;
    @Autowired
    RecoveryService recoveryService;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/withdrawal")
    public String withdrawal(){return "recovery/withdrawal";}

    @GetMapping("/recovery")
    public String recovery(){
        return "recovery/recovery";
    }

    @PostMapping("/withdrawalId")
    public String withdrawal(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        Long id = user.getId();
        recoveryService.withdrawalDateById(id);
        return "recovery/withdrawal";
    }


    // 이메일 인증
    @GetMapping(value = "/account/email")
    @ResponseBody
    public String checkEmail(String email) {
        System.out.println("받은 이메일 : " + email);

        String data = emailAuthService.joinEmail(email);
        return data;
    }

    @GetMapping(value = "/signup")
    public String signup(){
        return "recovery/signup";
    }

    @GetMapping(value = "/signout")
    public String signout(){
        return "recovery/signout";
    }

    @PostMapping(value = "/account/recovery")
    public ResponseEntity<String> recoveryStep(@RequestParam String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setWithdrawalDate(null);
            user.setRole(Role.USER);
            userRepository.save(user);
            return ResponseEntity.ok("계정 복구가 완료되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("계정을 찾을 수 없습니다.");
        }
    }
}

