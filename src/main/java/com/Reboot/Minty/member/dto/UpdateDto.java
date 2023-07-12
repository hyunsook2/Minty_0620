package com.Reboot.Minty.member.dto;

import com.Reboot.Minty.member.constant.Role;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UpdateDto {

    @NotEmpty(message = "닉네임은 필수 입력입니다.")
    private String nickName;

    private String password;

    private String email;

    private String name;

    private String ageRange;

    private String mobile;

    private String gender;

    private Role role;

    private String image;
}