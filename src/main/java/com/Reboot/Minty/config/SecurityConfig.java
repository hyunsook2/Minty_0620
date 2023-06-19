package com.Reboot.Minty.config;

import com.Reboot.Minty.member.dto.JoinDto;
import com.Reboot.Minty.member.service.CustomOAuth2UserService;
import com.Reboot.Minty.member.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;
    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .requestMatchers("/adimage/**","/css/**", "/js/**", "/image/**","/fragments/**","/layout/**").permitAll()
                .requestMatchers("/login/**", "/join/**", "/map/**","/","/sms/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().ignoringRequestMatchers(
                        new AntPathRequestMatcher("/h2-console/**")
                ).and().headers().addHeaderWriter(new XFrameOptionsHeaderWriter(
                        XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN
                )).and().formLogin().loginPage("/login").usernameParameter("email").successHandler(generalSuccessHandler()).failureUrl("/login/error")
                .and().oauth2Login(
                        oauth2Login -> oauth2Login
                                .loginPage("/login").successHandler(authenticationSuccessHandler())
                                .userInfoEndpoint(
                                        userInfoEndPoint -> userInfoEndPoint
                                                .userService(customOAuth2UserService)
                                )
                )
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/").invalidateHttpSession(true);

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler generalSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

        @Autowired
        private HttpSession session;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Long userId =  userService.getUserInfo(email).getId();
            String userNickName = userService.getUserInfo(email).getNickName();
            session.setAttribute("userId",userId);
            session.setAttribute("userEmail", email);
            session.setAttribute("userNickName",userNickName);
            response.sendRedirect("/");
        }
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            boolean hasRegisterUserAuthority = token.getAuthorities().stream()
                    .anyMatch(auth -> "REGISTER_USER".equals(auth.getAuthority()));
            OAuth2User oAuth2User = token.getPrincipal();
            Map<String, Object> kakao_account = (Map<String, Object>) oAuth2User.getAttribute("kakao_account");
            Map<String, Object> naver_account = (Map<String, Object>) oAuth2User.getAttribute("response");
            if (hasRegisterUserAuthority) {
                HttpSession session = request.getSession();
                if (kakao_account != null && kakao_account.containsKey("email")) {
                    JoinDto joinDto = new JoinDto();
                    joinDto.setName((String) kakao_account.get("name"));
                    joinDto.setEmail((String) kakao_account.get("email"));
                    joinDto.setAgeRange((String) kakao_account.get("age_range"));
                    joinDto.setMobile((String) kakao_account.get("phone_number"));
                    joinDto.setGender((String) kakao_account.get("gender"));
                    session.setAttribute("joinDto", joinDto);
                } else if (naver_account != null && naver_account.containsKey("email")) {
                    JoinDto joinDto = new JoinDto();
                    joinDto.setName((String) naver_account.get("name"));
                    joinDto.setEmail((String) naver_account.get("email"));
                    String age = (String) naver_account.get("age");
                    age = age.replace("-", "~");
                    joinDto.setAgeRange(age);
                    joinDto.setMobile((String) naver_account.get("mobile"));
                    String gender = (String) naver_account.get("gender");
                    if (gender.equals("M")) gender = "male";
                    else gender = "female";
                    joinDto.setGender(gender);
                    session.setAttribute("joinDto", joinDto);
                }

                response.sendRedirect("/join");
            }  else {
                HttpSession session = request.getSession();
                if (kakao_account != null && kakao_account.containsKey("email")) {
                    session.setAttribute("userEmail", userService.getUserInfo((String)kakao_account.get("email")).getEmail());
                    session.setAttribute("userId",userService.getUserInfo((String)kakao_account.get("email")).getId());
                    session.setAttribute("userNickName",userService.getUserInfo((String)kakao_account.get("email")).getNickName());

                }else{
                    session.setAttribute("userEmail", userService.getUserInfo((String)naver_account.get("email")).getEmail());
                    session.setAttribute("userId",userService.getUserInfo((String)naver_account.get("email")).getId());
                    session.setAttribute("userNickName",userService.getUserInfo((String)naver_account.get("email")).getNickName());

                }
                response.sendRedirect("/loginSuccess");
            }

        };
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}