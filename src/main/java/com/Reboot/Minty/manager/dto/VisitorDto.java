package com.Reboot.Minty.manager.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class VisitorDto {
    private Long id;
    private String ipAddress;
    private LocalDateTime  visitTime;
}

