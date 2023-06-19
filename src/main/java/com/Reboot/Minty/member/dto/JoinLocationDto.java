package com.Reboot.Minty.member.dto;

import com.Reboot.Minty.member.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class JoinLocationDto {
    private String latitude;
    private String longitude;
    private String address;
    private User user;

}
