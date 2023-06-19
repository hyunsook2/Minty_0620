package com.Reboot.Minty.member.service;

import com.Reboot.Minty.member.constant.Role;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.oauth2.OAuthAttributes;
import com.Reboot.Minty.member.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.net.http.HttpRequest;
import java.util.Collections;

@Service

public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {


    @Autowired
    private UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest);

        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        User user = userRepository.findByEmail(attributes.getEmail());

        if (user == null) {
            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(Role.REGISTER_USER.name())),
                    attributes.getAttributes(),
                    attributes.getNameAttributeKey()
            );
        } else {
            // 현재 인증 정보 확인
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                // 인증 정보가 없으면 새로 생성하여 설정
                authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.singleton(new SimpleGrantedAuthority(Role.USER.name())));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(Role.USER.name())),
                    attributes.getAttributes(),
                    attributes.getNameAttributeKey());
        }
    }


}