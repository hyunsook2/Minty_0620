package com.Reboot.Minty.member.dto;

import lombok.Data;

@Data
public class VerificationRequest {
    private String verificationCode;

    public String getVerificationCode() {
        return verificationCode;
    }
}
