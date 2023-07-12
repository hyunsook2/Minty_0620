package com.Reboot.Minty.member.dto;

import com.Reboot.Minty.member.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String email;
    private String nickName;

    private static ModelMapper modelMapper = new ModelMapper();

    public static UserResponseDto of(User user){
        return modelMapper.map(user,UserResponseDto.class);
    }
}