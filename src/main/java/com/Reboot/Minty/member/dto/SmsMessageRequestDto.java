package com.Reboot.Minty.member.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class SmsMessageRequestDto {
    String type;
    String contentType;
    String countryCode;
    String from;
    String content;
    List<SmsMessageDto> messages;
}
