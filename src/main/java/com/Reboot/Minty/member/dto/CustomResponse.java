package com.Reboot.Minty.member.dto;

import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class CustomResponse {

    private String verificationCode;
    private Duration verificationTimeLimit;
    private SmsMessageResponseDto smsResponse;
}
