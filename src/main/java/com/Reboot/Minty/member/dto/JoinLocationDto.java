package com.Reboot.Minty.member.dto;

import com.Reboot.Minty.member.constant.UserLocationStatus;
import com.Reboot.Minty.member.entity.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class JoinLocationDto {
    private String latitude;
    private String longitude;
    private String address;
    private User user;

    private String representativeYN;
    @Enumerated(EnumType.STRING)
    private UserLocationStatus userLocationStatus;
    private Timestamp modifiedDate;

}