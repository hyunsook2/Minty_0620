package com.Reboot.Minty.chat.dto;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatRoomDTO {

    private Long myId;
    private Long otherId;
    private User my;
    private User other;
    private TradeBoard tradeBoard;
}
