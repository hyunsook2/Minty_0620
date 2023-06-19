package com.Reboot.Minty.manager.dto;

import com.Reboot.Minty.support.entity.Ad;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AdDto {
    private Long id;
    private String title;
    private String advertiserName;
    private LocalDate requestDate;
    private String status;
    private int totalPages;

    public AdDto convertToDTO(Ad ad) {
        setId(ad.getId());
        setTitle(ad.getTitle());
        setAdvertiserName(ad.getAdvertiserName());
        setRequestDate(ad.getRequestDate());
        setStatus(ad.getStatus());
        return this;
    }
}
