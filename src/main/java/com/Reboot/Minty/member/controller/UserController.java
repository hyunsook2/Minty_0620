package com.Reboot.Minty.member.controller;

import com.Reboot.Minty.member.dto.*;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserLocationRepository;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.member.service.JoinFormValidator;
import com.Reboot.Minty.member.service.SmsService;
import com.Reboot.Minty.member.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;
    private final UserRepository userRepository;
    private final JoinFormValidator joinFormValidator;
    private final UserLocationRepository userLocationRepository;

    @Autowired
    private Storage storage;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, SmsService smsService, UserRepository userRepository, JoinFormValidator joinFormValidator, UserLocationRepository userLocationRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.smsService = smsService;
        this.userRepository = userRepository;
        this.joinFormValidator = joinFormValidator;
        this.userLocationRepository = userLocationRepository;
    }

    @Value("${spring.cloud.gcp.storage.credentials.bucket}")
    private String bucketName;

    @GetMapping("/update")
    public String updateForm(Model model, HttpSession session) {
        JoinDto joinDto = (JoinDto) session.getAttribute("joinDto");
        session.removeAttribute("joinDto");
        if (joinDto != null) {
            String mobile = joinDto.getMobile();
            if (mobile != null) {
                mobile = mobile.replace("+82", "0").replaceAll("\\s|-", "");
                joinDto.setMobile(mobile);
            }
            System.out.println(joinDto);
            model.addAttribute("joinDto", joinDto);
            model.addAttribute("readOnly", true);
        } else {
            model.addAttribute("joinDto", new JoinDto());
            model.addAttribute("readOnly", false);
        }

        return "member/join";
    }

    @GetMapping("/login")
    public String login() {
        return "member/login";
    }


    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "member/login";
    }

    @GetMapping("/join")
    public String joinForm(Model model, HttpSession session) {
        JoinDto joinDto = (JoinDto) session.getAttribute("joinDto");
        session.invalidate();
        if (joinDto != null) {
            String mobile = joinDto.getMobile();
            if (mobile != null) {
                mobile = mobile.replace("+82", "0").replaceAll("\\s|-", "");
                joinDto.setMobile(mobile);
            }
            System.out.println(joinDto);
            model.addAttribute("joinDto", joinDto);
            model.addAttribute("readOnly", true);
        } else {
            model.addAttribute("joinDto", new JoinDto());
            model.addAttribute("readOnly", false);
        }
        return "member/join";
    }

    @PostMapping("/join")
    public String joinSubmit(@Valid JoinDto joinDto, BindingResult bindingResult, Model model, HttpSession session, Errors errors) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("JoinDto", joinDto);
            return "member/join";
        }
        joinFormValidator.validate(joinDto, errors);
        if (errors.hasErrors()) {
            model.addAttribute("JoinDto", joinDto);
            return "member/join";
        }
        try {
            User user = User.saveUser(joinDto, passwordEncoder);

            if(joinDto.getGender().equals("female")){
                user.setImage("aaaa.png");
            } else {
                user.setImage("aaa.png");
            }

            User savedUser = userService.saveUser(user);
            return "redirect:/login";
        } catch (IllegalStateException e) {
            model.addAttribute("JoinDto", joinDto);
            model.addAttribute("errorMessage", e.getMessage());
            return "member/join";
        }
    }

    @Value("${kaKao-jsKey}")
    private String kaKaoKey;

    @GetMapping("/map")
    public String getMap(HttpSession session, Model model) {
        try {
            User user = userRepository.findById((Long) session.getAttribute("userId"))
                    .orElseThrow(EntityNotFoundException::new);
            long count = userLocationRepository.countByUserId(user.getId());
            if (count >= 3) {
                throw new IllegalStateException("유저 지역 정보는 최대 3개까지만 가능합니다.");
            }
            session.setAttribute("user", user);
            model.addAttribute("kaKaoKey", kaKaoKey);
            return "map/map";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "map/map";
        }
    }

    @PostMapping("/saveLocation")
    public String saveLocation(@ModelAttribute JoinLocationDto joinLocationDto, HttpSession session, CsrfToken csrfToken) {
        System.out.println("saveLocation method()");
        // Get location and address information
        String csrfTokenValue = csrfToken.getToken();
        String csrfHeaderName = csrfToken.getHeaderName();

        System.out.println(joinLocationDto.getAddress());

        HttpHeaders headers = new HttpHeaders();
        headers.set(csrfHeaderName, csrfTokenValue);
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        String latitude = joinLocationDto.getLatitude();
        String longitude = joinLocationDto.getLongitude();
        String address = joinLocationDto.getAddress();
        User user = userRepository.findById((Long)session.getAttribute("userId")).orElseThrow(EntityNotFoundException::new);
        userService.saveUserLocation(user, latitude, longitude, address);
        return "redirect:/";
    }

    @GetMapping("/api/isLoggedIn")
    @ResponseBody
    public Map<String,Object> isLoggedIn(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean LoggedIn = authentication.isAuthenticated() ? true : false;
        response.put("LoggedIn", LoggedIn);
        if(LoggedIn){
            HttpSession session = request.getSession();
            String userRole = (String)session.getAttribute("userRole");
            response.put("userRole",userRole);
        }
        return response;
    }

    @PostMapping("/sms/send")
    @ResponseBody
    public Mono<CustomResponse> sendSms(@RequestBody String mobileNumber, HttpServletRequest request) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        System.out.println("sendSms 컨트롤러");
        int isExistNumber = userRepository.countByMobile(mobileNumber);
        if (isExistNumber >= 1) {
            return Mono.error(new IllegalStateException("해당 핸드폰 번호는 이미 존재합니다."));
        }

        Mono<CustomResponse> response = smsService.sendSms(mobileNumber)
                .doOnSuccess(customResponse -> {
                    HttpSession session = request.getSession();
                    session.setAttribute("verificationCode", customResponse.getVerificationCode());
                    session.setAttribute("verificationTimeLimit", customResponse.getVerificationTimeLimit());
                    // 만료 시간 설정
                    Duration verificationTimeLimit = customResponse.getVerificationTimeLimit();
                    LocalDateTime expirationTime = LocalDateTime.now().plus(verificationTimeLimit);
                    session.setAttribute("verificationExpirationTime", expirationTime);
                });
        System.out.println(response.toString());
        return response;
    }

    @ResponseBody
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalStateException(IllegalStateException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        return errorResponse;
    }


    @PostMapping("/sms/verify")
    @ResponseBody
    public ResponseEntity<?> verifyCode(@RequestBody VerificationRequest verificationRequest, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String storedVerificationCode = (String) session.getAttribute("verificationCode");
        Duration verificationTimeLimit = (Duration) session.getAttribute("verificationTimeLimit");
        LocalDateTime expirationTime = (LocalDateTime) session.getAttribute("verificationExpirationTime");
        String userEnteredCode = verificationRequest.getVerificationCode(); // Get the entered verification code
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println("유저 입력값>>" + userEnteredCode + "\n");
        System.out.println("발행 코드 " + storedVerificationCode + "\n");
        if (currentTime.isAfter(expirationTime)) {
            session.invalidate();
            return ResponseEntity.ok().body("{Verification code expired.}");
        } else {
            if (userEnteredCode.equals(storedVerificationCode)) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.ok(false);
            }
        }
    }

    @GetMapping("/edit")
    public String showEditForm(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId);

        if (user != null) {
            UpdateDto updateDto = new UpdateDto();
            updateDto.setEmail(user.getEmail());
            updateDto.setPassword(user.getPassword());
            updateDto.setName(user.getName());
            updateDto.setNickName(user.getNickName());
            updateDto.setAgeRange(user.getAgeRange());
            updateDto.setMobile(user.getMobile());
            updateDto.setGender(user.getGender());

            model.addAttribute("updateDto", updateDto);
        } else {
            model.addAttribute("updateDto", new UpdateDto());
        }

        return "member/edit";
    }

    @PostMapping("/edit")
    public String editMember(@Valid @ModelAttribute UpdateDto updateDto, BindingResult bindingResult, HttpSession session, Model model, @RequestParam("imageFile") MultipartFile imageFile) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId);
        if (!imageFile.isEmpty()) {
            try {
                String uuid = UUID.randomUUID().toString();
                BlobInfo blobInfo = storage.create(
                        BlobInfo.newBuilder(bucketName, uuid)
                                .setContentType("image/jpg")
                                .build(),
                        imageFile.getInputStream()
                );
                user.setImage(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        userService.saveUser(user);

        if (bindingResult.hasErrors()) {
            return "member/edit";
        }

        if (user != null) {
            try {
                // 비밀번호만 수정하는 경우
                if (!updateDto.getPassword().isEmpty() && updateDto.getNickName().isEmpty()) {
                    user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
                }
                // 닉네임만 수정하는 경우
                else if (updateDto.getPassword().isEmpty() && !updateDto.getNickName().isEmpty()) {
                    user.setNickName(updateDto.getNickName());
                }
                // 비밀번호와 닉네임을 함께 수정하는 경우
                else {
                    user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
                    user.setNickName(updateDto.getNickName());
                }

                user.setName(updateDto.getName());
                user.setAgeRange(updateDto.getAgeRange());
                user.setMobile(updateDto.getMobile());
                user.setGender(updateDto.getGender());
                userRepository.save(user);
                model.addAttribute("updateSuccessMessage", "회원 정보가 성공적으로 업데이트되었습니다.");
            } catch (Exception e) {
                model.addAttribute("updateErrorMessage", "회원 정보를 업데이트하는 중에 오류가 발생했습니다.");
            }
        } else {
            model.addAttribute("updateErrorMessage", "회원 정보를 찾을 수 없습니다.");
        }

        return "member/edit";
    }

}