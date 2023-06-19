package com.Reboot.Minty.manager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@Entity
@Table(name = "ManagerStatistics")
public class ManagerStatistics {
    @Id
    private LocalDate visitDate;

    private int transaction;

    private int sales;

    private int visitor;

    private int register;

    private int inquiry;

    private int review;


}
