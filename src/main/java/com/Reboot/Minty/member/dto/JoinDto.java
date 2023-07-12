package com.Reboot.Minty.member.dto;

import com.Reboot.Minty.member.constant.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class JoinDto {

    @NotEmpty(message = "이메일은 필수 입력입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "이름은 필수 입력입니다.")
    private String name;

    @NotEmpty(message = "비밀번호는 필수 입력입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[$@$!%*#?&])[a-zA-Z\\d$@$!%*#?&]{8,16}$", message = "비밀번호는 8~16자리수여야 합니다. 영문 (대문자 또는 소문자), 숫자, 특수문자를 1개 이상 포함해야 합니다.")
    private String password;

    @NotEmpty(message = "비밀번호 확인은 필수 입력입니다.")
    private String passwordConfirm;

    @NotEmpty(message = "닉네임은 필수 입력입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$" , message = "닉네임은 특수문자를 포함하지 않은 2~10자리여야 합니다.")
    private String nickName;

    @Column(name="age_range")
    @NotEmpty(message = "필수")
    private String ageRange;

    @NotEmpty(message = "필수")
    @Pattern(regexp = "\\d+", message = "핸드폰은 숫자로만 입력해주세요.")
    private String mobile;

    @NotEmpty(message = "필수")
    private String gender;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

//   @AssertTrue(message = "핸드폰 인증이 필요합니다.")
//    public Boolean getVerified() {
//        return isVerified;
//    }
//    @NotNull(message = "핸드폰 인증이 필요합니다.")
//    private Boolean isVerified;
}