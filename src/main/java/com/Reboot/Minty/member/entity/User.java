package com.Reboot.Minty.member.entity;


import com.Reboot.Minty.member.constant.Role;
import com.Reboot.Minty.member.dto.JoinDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Table(name = "user")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false, name = "ageRange")
    private String ageRange;

    @Column(nullable = false, name = "mobile")
    private String mobile;

    @Column(nullable = false, name = "gender")
    private String gender;

    @Column
    private String image;

    // 레벨, 경험치, 잔액 추가
    @Column(nullable = false, name = "level")
    private int level;
    @Column(nullable = false, name = "exp")
    private int exp;
    @Column(nullable = false, name = "balance")
    private int balance;
    @Column(nullable = false, name = "point")
    private int point;
    @Column
    private LocalDate withdrawalDate;
    public User(String name, String email, String ageRange, String mobile, String gender) {
        this.name = name;
        this.email = email;
        this.ageRange = ageRange;
        this.mobile = mobile;
        this.gender = gender;
    }

    public static User saveUser(JoinDto joinDto, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setEmail(joinDto.getEmail());
        user.setName(joinDto.getName());
        user.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        user.setNickName(joinDto.getNickName());
        user.setAgeRange(joinDto.getAgeRange());
        user.setMobile(joinDto.getMobile());
        user.setGender(joinDto.getGender());
        user.setRole(Role.USER);
        user.setLevel(1);
        user.setExp(0);
        user.setBalance(0);
        user.setPoint(0);
        return user;
    }
    public void setWithdrawalDateToNow() {
        withdrawalDate = LocalDate.now();
    }
}