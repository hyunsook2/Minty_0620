
package com.Reboot.Minty.event.controller;

import com.Reboot.Minty.event.dto.RouletteDto;
import com.Reboot.Minty.event.entity.Roulette;
import com.Reboot.Minty.event.service.RouletteService;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RouletteController {
    private final RouletteService rouletteService;
    private final UserRepository userRepository;

    @Autowired
    public RouletteController(RouletteService rouletteService, UserRepository userRepository) {
        this.rouletteService = rouletteService;
        this.userRepository = userRepository;
    }

    @GetMapping("/roulette")
    public String rouletteForm(Model model, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            model.addAttribute("userId", userId);
            model.addAttribute("message", "룰렛돌리기ㅎㅇ");
            return "event/roulette";
        } catch (Exception e) {
            model.addAttribute("error", "요청을 처리하는 중에 오류가 발생했습니다.");
            e.printStackTrace();
            return "home";
        }
    }

    @PostMapping("/roulette/save")
    @ResponseBody
    public Roulette saveRoulette(@RequestBody RouletteDto rouletteDto, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                System.out.println("사용자를 찾을 수 없습니다.");
                return null;
            }

            int currentPoint = user.getPoint();
            int roulettePoint = rouletteDto.getPoint();
            int updatedPoint = currentPoint + roulettePoint;
            user.setPoint(updatedPoint);

            Roulette roulette = new Roulette();
            roulette.setUser(user);
            roulette.setResult(rouletteDto.getResult());
            roulette.setPoint(rouletteDto.getPoint());

            System.out.println("Saving roulette: " + roulette);
            Roulette savedRoulette = rouletteService.saveRoulette(roulette);
            System.out.println("Saved roulette: " + savedRoulette);

            return savedRoulette;
        } catch (Exception e) {
            System.out.println("Error saving roulette: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
