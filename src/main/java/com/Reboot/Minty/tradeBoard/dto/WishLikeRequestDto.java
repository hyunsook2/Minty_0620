package com.Reboot.Minty.tradeBoard.dto;


import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishLikeRequestDto {

    private User userId;
    private TradeBoard postId;

}
