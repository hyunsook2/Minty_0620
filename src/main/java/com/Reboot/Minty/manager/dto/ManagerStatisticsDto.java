package com.Reboot.Minty.manager.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class ManagerStatisticsDto {
    private LocalDate visitDate;
    private int transaction;
    private int sales;
    private int visitor;
    private int register;
    private int inquiry;
    private int review;
}