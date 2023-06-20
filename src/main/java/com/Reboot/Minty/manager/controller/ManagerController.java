package com.Reboot.Minty.manager.controller;

import com.Reboot.Minty.manager.dto.ManagerStatisticsDto;
import com.Reboot.Minty.manager.entity.Admin;
import com.Reboot.Minty.manager.entity.ManagerStatistics;
import com.Reboot.Minty.manager.repository.AdminRepository;
import com.Reboot.Minty.manager.service.ManagerStatisticsService;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.support.entity.Ad;
import com.Reboot.Minty.support.repository.AdRepository;
import com.Reboot.Minty.support.service.AdService;

import io.jsonwebtoken.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.time.LocalDate;
import java.util.*;

@Controller
public class ManagerController {
    private final ManagerStatisticsService managerStatisticsService;
    private final AdService adService;
    private final AdRepository adRepository;
    private final AdminRepository adminRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public ManagerController(ManagerStatisticsService managerStatisticsService, AdService adService, AdRepository adRepository, AdminRepository adminRepository, UserService userService, UserRepository userRepository) {
        this.managerStatisticsService = managerStatisticsService;
        this.adService = adService;
        this.adRepository = adRepository;
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping(value = "/manager")
    public String manager(Model model, Pageable pageable) {
        List<ManagerStatisticsDto> statisticsList = managerStatisticsService.getAllManagerStatistics();
        List<Ad> ads = adService.getAllAds();
        Collections.reverse(ads);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 객체에서 로그인된 사용자의 정보를 가져옴
        User loggedInUser = userRepository.findByEmail(authentication.getName());

        Page<User> postList = userService.getPostList(pageable);
        model.addAttribute("userList", postList.getContent());
        model.addAttribute("user", loggedInUser);
        model.addAttribute("postList", postList);
        model.addAttribute("pageable", pageable);
        model.addAttribute("statisticsList", statisticsList);
        model.addAttribute("ads", ads);
        return "manager/dashboard";
    }

    private void sendEmail(String recipientEmail, String subject, String content) {
        // 이메일 세션 생성
        Session session = null;
        try {
            // SMTP 서버 설정
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.naver.com");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.ssl.enable", "true"); // SSL 보안 연결 설정

            // 발신자 이메일 계정 정보
            String senderEmail = "k_hyojin82@naver.com";
            String senderPassword = "Kkbsc3982#";

            session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            // 이메일 메시지 생성
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(content);

            // 이메일 발송
            Transport.send(message);

            System.out.println("이메일이 성공적으로 발송되었습니다.");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("이메일 발송 중 오류가 발생했습니다.");
        } finally {
            if (session != null) {
                try {
                    session.getTransport().close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @PostMapping("/admin/ads/{id}/approve")
    @ResponseBody
    public String approveAd(@PathVariable("id") Long id) {
        adService.updateAdStatus(id, "승인");
        saveAdToAdmin(id);

        // 이메일 발송
        try {
            Optional<Ad> adOptional = adRepository.findById(id);
            if (adOptional.isPresent()) {
                Ad ad = adOptional.get();
                String recipientEmail = ad.getEmail();
                if (recipientEmail != null) {
                    String subject = "광고 승인 안내";
                    String content = ad.getAdvertiserName() + "님이 요청하신 광고가 승인되었습니다.";
                    sendEmail(recipientEmail, subject, content);
                    ResponseEntity.ok();

                    // 매출 누적
                    int amount = ad.getAmount();
                    LocalDate today = LocalDate.now();

                    ManagerStatistics statistics = managerStatisticsService.getStatisticsByVisitDate(today);
                    if (statistics == null) {
                        statistics = new ManagerStatistics();
                        statistics.setVisitDate(today);
                        statistics.setSales(amount);
                        // Set other fields if necessary
                        managerStatisticsService.saveStatistics(statistics);
                    } else {
                        int currentSales = statistics.getSales();
                        statistics.setSales(currentSales + amount);
                        managerStatisticsService.saveStatistics(statistics);
                    }
                } else {
                    System.out.println("광고에 이메일 주소가 없습니다.");
                }
            } else {
                System.out.println("광고를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("이메일 발송 중 오류가 발생했습니다.");
        }

        return "";
    }


    @PostMapping("/admin/ads/{id}/reject")
    @ResponseBody
    public String rejectAd(@PathVariable("id") Long id) {
        adService.updateAdStatus(id, "거부");
        saveAdToAdmin(id);

        // 이메일 발송
        Optional<Ad> adOptional = adRepository.findById(id);
        if (adOptional.isPresent()) {
            Ad ad = adOptional.get();
            String recipientEmail = ad.getEmail();
            if (recipientEmail != null) {
                String subject = "광고 거절 안내";
                String content = ad.getAdvertiserName() + "님이 요청하신 광고가 거절되었습니다.";
                sendEmail(recipientEmail, subject, content);
            } else {
                System.out.println("광고에 이메일 주소가 없습니다.");
            }
        } else {
            System.out.println("광고를 찾을 수 없습니다.");
        }

        return "";
    }

    // Admin 엔티티에 저장하는 메소드 추가
    private void saveAdToAdmin(Long adId) {
        try {
            Ad ad = adRepository.findById(adId).orElse(null);

            if (ad != null && ad.getStatus().equals("승인")) {
                Admin admin = new Admin();
                admin.setAdvertiserName(ad.getAdvertiserName());
                admin.setAmount(ad.getAmount());
                admin.setEmail(ad.getEmail());
                admin.setEndDate(ad.getEndDate());
                admin.setStartDate(ad.getStartDate());
                admin.setImage(ad.getImage());
                admin.setStatus(ad.getStatus());

                ad.setAdmin(admin); // Ad 엔티티와 Admin 엔티티 간의 관계를 설정

                adminRepository.save(admin);
            }
        } catch (Exception e) {
            e.printStackTrace(); // 예외 정보를 콘솔에 출력
        }
    }

    @GetMapping("/adAdmin/{id}")
    public String adAdminPage(@PathVariable("id") Long id, Model model) {
        Optional<Ad> adOptional = adService.getAdById(id);
        if (adOptional.isPresent()) {
            Ad ad = adOptional.get();
            model.addAttribute("ad", ad);
            return "ad/adAdmin";
        } else {
            return "ad/dashboard";
        }
    }

    @GetMapping("/mainPage")
    public String mainPage(Model model) {
        List<Ad> approvedAds = adRepository.findByStatus("승인");

        List<Ad> approvedImages = new ArrayList<>();

        for (Ad ad : approvedAds) {
            String imagePath = "adimage/" + ad.getImage();
            Resource resource = new ClassPathResource("static/" + imagePath);

            try {
                if (resource.exists()) {
                    approvedImages.add(ad);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!approvedImages.isEmpty()) {
            int randomIndex = (int) (Math.random() * approvedImages.size());
            Ad ad = approvedImages.get(randomIndex);
            model.addAttribute("ad", ad);

            String imagePath = "adimage/";
            model.addAttribute("imagePath", imagePath);
        }

        return "ad/mainPage";
    }

    @PostMapping(value = "/manager", produces = "application/json")
    @ResponseBody
    public List<Ad> searchAds(@RequestBody Map<String, String> requestBody) {
        String keyword = requestBody.get("keyword");
        System.out.println(keyword);

        List<Ad> ads;
        if (keyword != null && !keyword.isEmpty()) {
            ads = adService.searchAdsByAdvertiserName(keyword);
        } else {
            ads = adService.getAllAds();
        }
        return ads;
    }

}