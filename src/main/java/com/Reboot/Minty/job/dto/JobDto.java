package com.Reboot.Minty.job.dto;

import com.Reboot.Minty.job.entity.Job;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
@Getter
@Setter
public class JobDto {
    private Long id;
    private String title;
    private Timestamp createdDate;
    private String jobLocation;
    private int payTotal;
    private String thumbnail;

    @QueryProjection
    public JobDto(Long id, String title, Timestamp createdDate,
                  String jobLocation, int payTotal, String thumbnail) {
        this.id = id;
        this.title = title;
        this.createdDate = createdDate;
        this.jobLocation = jobLocation;
        this.payTotal = payTotal;
        this.thumbnail = thumbnail;
    }
}
