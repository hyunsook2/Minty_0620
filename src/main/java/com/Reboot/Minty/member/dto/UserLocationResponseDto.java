package com.Reboot.Minty.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLocationResponseDto {
    private Long id;
    private String address;
    private UserResponseDto userId;
}
