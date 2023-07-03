package com.Reboot.Minty.job.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@DynamicInsert
public class Job {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdDate;

    @Column(nullable = false)
    private String jobLocation;

    @Column(nullable = false)
    private String payType;

    @Column(nullable = false)
    private int payTotal;

    @Column(nullable = false)
    private String thumbnail;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<JobImage> jobImages = new ArrayList<>();
}
