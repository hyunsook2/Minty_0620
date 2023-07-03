package com.Reboot.Minty.member.dto;

import com.Reboot.Minty.member.entity.UserLocation;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class UserLocationResponseDto {
    private Long id;
    private String address;
    private UserResponseDto userId;

    private static ModelMapper modelMapper = new ModelMapper();

    public static UserLocationResponseDto of (UserLocation userLocation){
        return modelMapper.map(userLocation,UserLocationResponseDto.class);
    }
}
