package com.Reboot.Minty.interceptor;

import com.Reboot.Minty.support.entity.Ad;
import com.Reboot.Minty.support.repository.AdRepository;
import com.Reboot.Minty.support.service.AdService;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class GlobalDataInterceptor implements HandlerInterceptor {

    private final AdRepository adRepository;

    public GlobalDataInterceptor(AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null && !modelAndView.isEmpty()) {
            List<Ad> approvedAds = adRepository.findByStatusIn(List.of("승인", "게시중"));
            int size = approvedAds.size();
            Random random = new Random();
            List<Ad> approvedImages = new ArrayList<>();
            LocalDateTime currentDateTime = LocalDateTime.now();

            for (Ad ad : approvedAds) {
                String imagePath = "adimage/" + ad.getImage();
                Resource resource = new ClassPathResource("static/" + imagePath);
                try {
                    if (resource.exists() && currentDateTime.isAfter(ad.getStartDate().atStartOfDay()) && currentDateTime.isBefore(ad.getEndDate().atTime(23, 59, 59))) {
                        approvedImages.add(ad);
                    } else if (currentDateTime.isAfter(ad.getEndDate().atTime(23, 59, 59))) {
                        ad.setStatus("게시종료");
                        adRepository.save(ad);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (!approvedImages.isEmpty()) {
                int randomIndex = random.nextInt(approvedImages.size());
                Ad ad = approvedImages.get(randomIndex);
                modelAndView.addObject("advertise", ad);
                ad.setStatus("게시중");
                adRepository.save(ad);
            } else {
                modelAndView.addObject("advertise", null);
            }
        } else {
            modelAndView = new ModelAndView();
            modelAndView.addObject("advertise", null);
        }
    }

}