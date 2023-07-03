package com.Reboot.Minty.member.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class SmsMessageDto {
    String to;
    String content;
}
