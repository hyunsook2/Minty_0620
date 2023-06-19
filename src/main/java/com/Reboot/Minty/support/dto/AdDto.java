package com.Reboot.Minty.support.dto;

import com.Reboot.Minty.support.entity.Ad;

import java.time.LocalDate;

public class AdDto {
    private Long id;
    private String advertiserName;
    private String title;
    private String content;
    private String image;
    private String email;
    private LocalDate startDate;
    private LocalDate endDate;
    private int amount;

}
