package com.Reboot.Minty.tradeBoard.dto;

import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToggleLikeRequestDto {
    private long id;
    private Boolean isLiked;
}

