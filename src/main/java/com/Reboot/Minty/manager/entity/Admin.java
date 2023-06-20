package com.Reboot.Minty.manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
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
