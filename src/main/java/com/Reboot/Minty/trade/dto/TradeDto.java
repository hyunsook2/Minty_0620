package com.Reboot.Minty.trade.dto;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TradeDto {
    private Long id;
    private TradeBoard boardId;
    private User sellerId;
    private User buyerId;
    private LocalDateTime startDate;
    private LocalDateTime tradeDate;
    private LocalDateTime endDate;
    private String mode;
    private String status;
    private String sellerCheck;
    private String buyerCheck;
}
