package com.Reboot.Minty.support.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    String sendSimpleMessage(String to)throws Exception;
}
