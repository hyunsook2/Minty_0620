package com.Reboot.Minty.job.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class JobImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Job job;

    @Column(nullable = false)
    private String imgUrl;


}
