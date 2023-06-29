package com.Reboot.Minty.interceptor;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

public class UserLocationInterceptor implements HandlerInterceptor {


    private final UserService userService;

    public UserLocationInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long userId;
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/loginSuccess")) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) request.getUserPrincipal();
            OAuth2User oAuth2User = token.getPrincipal();
            Map<String, Object> kakao_account = (Map<String, Object>) oAuth2User.getAttribute("kakao_account");
            Map<String, Object> naver_account = (Map<String, Object>) oAuth2User.getAttribute("response");
            if (kakao_account != null) {
                userId = userService.getUserId((String) kakao_account.get("email"));
            } else {
                userId = userService.getUserId((String) naver_account.get("email"));
            }
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails user = (UserDetails) authentication.getPrincipal();
            System.out.println(user);
            System.out.println(user.getUsername());
            userId = userService.getUserId(user.getUsername());
        }

        boolean userHasLocation = userService.userHasLocation(userId);
        if (userHasLocation) {
            response.sendRedirect("/");
        } else {
            response.sendRedirect("/map");
        }
        return false;
    }
}