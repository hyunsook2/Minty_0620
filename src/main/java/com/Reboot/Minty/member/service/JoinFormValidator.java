package com.Reboot.Minty.member.service;


import com.Reboot.Minty.member.dto.JoinDto;
import com.Reboot.Minty.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class JoinFormValidator implements Validator {

    private final UserRepository userRepository;
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(JoinDto.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        JoinDto joinDto = (JoinDto)object;

        if(userRepository.existsByEmail(joinDto.getEmail())){
            errors.rejectValue("email", "invalid.email",
                    new Object[]{joinDto.getEmail()}, "이미 사용중인 이메일 입니다.");
        }

        if(userRepository.existsByNickName(joinDto.getNickName())){
            errors.rejectValue("nickName", "invalid.nickName",
                    new Object[]{joinDto.getNickName()}, "이미 사용중인 닉네임 입니다.");
        }

        if (!joinDto.getVerified()) {
            errors.rejectValue("verified", "invalid.verified", "핸드폰 인증이 필요합니다.");
        }
        if(userRepository.existsByMobile(joinDto.getMobile())){
            errors.rejectValue("mobile", "invalid.mobile",
                    new Object[]{joinDto.getMobile()}, "이미 사용중인 핸드폰 번호입니다.");
        }
    }
}