package com.Reboot.Minty.support.entity;

import com.Reboot.Minty.manager.entity.Admin;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter @Setter
@Entity
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String advertiserName;
    private String title;
    private String content;
    private String image;
    private LocalDate requestDate;
    private String email;
    @Column(nullable = false)
    private String status = "대기";
    private LocalDate startDate;
    private LocalDate endDate;
    private int amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Transient
    public int getDuration() {
        if (startDate != null && endDate != null) {
            return (int) ChronoUnit.DAYS.between(startDate, endDate);
        }
        return 0;
    }

}
