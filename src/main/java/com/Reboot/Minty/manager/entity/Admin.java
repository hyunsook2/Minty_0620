package com.Reboot.Minty.manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String advertiserName;
    private int amount;
    private String email;
    private LocalDate endDate;
    private LocalDate startDate;
    private String image;
    private String status;
}