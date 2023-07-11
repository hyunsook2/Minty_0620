package com.Reboot.Minty.chat.controller;

import com.Reboot.Minty.chat.dto.ChatRoomDTO;
import com.Reboot.Minty.chat.service.ChatRoomService;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.trade.repository.TradeRepository;
import com.Reboot.Minty.trade.service.TradeService;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import com.Reboot.Minty.tradeBoard.repository.TradeBoardRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatRoomController {

    @Autowired
    private UserService userService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradeBoardRepository tradeBoardRepository;


    @PostMapping("/chatRoom")
    public void chatRoom(@RequestBody Long tradeBoardId, HttpSession session, ChatRoomDTO chatRoomDTO) {


        TradeBoard tradeBoard = tradeBoardRepository.findById(tradeBoardId).orElseThrow(EntityNotFoundException::new);


        User buyer = userService.getUserInfoById((Long) session.getAttribute("userId"));
        User seller = userService.getUserInfoById(tradeBoard.getUser().getId());
        tradeService.save(tradeBoard, buyer, seller);

        Long my = userService.getUserInfoById((Long) session.getAttribute("userId")).getId();
        Long other = userService.getUserInfoById(tradeBoard.getUser().getId()).getId();

        chatRoomDTO.setMyId(my);
        chatRoomDTO.setOtherId(other);
        chatRoomDTO.setTradeBoard(tradeBoard);

        chatRoomService.saveChatRoom(chatRoomDTO, buyer);


    }

}
