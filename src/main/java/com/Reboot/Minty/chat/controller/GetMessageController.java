package com.Reboot.Minty.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GetMessageController {
    @GetMapping("/getchatting")
    public String chat() {
        return "chat/chat-app";
    }
}
