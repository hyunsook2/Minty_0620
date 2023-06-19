package com.Reboot.Minty.manager.controller;

import com.Reboot.Minty.manager.service.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/twilio")
public class TwilioController {
    private final TwilioService twilioService;

    @Autowired
    public TwilioController(TwilioService twilioService) {
        this.twilioService = twilioService;
    }

    @GetMapping("/send-sms")
    public String sendSms() {
        String phoneNumber = "+1234567890"; // 대상 전화번호 입력
        String message = "This is a test message from Twilio."; // 전송할 메시지 입력

        twilioService.sendSms(phoneNumber, message);

        return "redirect:/";
    }
}
