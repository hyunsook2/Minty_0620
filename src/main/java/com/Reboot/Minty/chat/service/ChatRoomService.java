package com.Reboot.Minty.chat.service;

import com.Reboot.Minty.chat.Entity.ChatRoom;
import com.Reboot.Minty.chat.Entity.Products;
import com.Reboot.Minty.chat.dto.ChatRoomDTO;
import com.Reboot.Minty.chat.repository.ChatRoomRepository;
import com.Reboot.Minty.chat.repository.ProductsRepository;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.trade.service.TradeService;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatRoomService {

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private TradeService tradeService;

    public void saveChatRoom(ChatRoomDTO chatRoomDTO, User buyer) {

        User my = userService.getUserInfoById(chatRoomDTO.getMyId());
        User other = userService.getUserInfoById(chatRoomDTO.getOtherId());
        TradeBoard tradeBoard = chatRoomDTO.getTradeBoard();

        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+tradeBoard.getId());

        Trade trade = tradeService.findTradeBoardById(tradeBoard,buyer);

        System.out.println(my.getId());
        System.out.println(other.getId());
        System.out.println(my.getNickName());
        System.out.println(trade.getId());


        if (!productsRepository.existsByMyAndOtherAndTrade(my,other,trade)){

            Products products = new Products();
            products.setOther(other);
            products.setMy(my);
            products.setTradeBoard(tradeBoard);
            products.setTrade(trade);
            products.setCreateTime(LocalDateTime.now());

            productsRepository.save(products);
        }



        ChatRoom chatRoom = new ChatRoom();
        // 중복 체크
        if (my.getId().equals(other.getId())) {
            System.out.println("구매자와 판매자가 같아");
            throw new IllegalArgumentException("구매자와 판매자가 같은 경우 처리");
        }


        if (chatRoomRepository.existsByMyAndOther(my,other) || chatRoomRepository.existsByOtherAndMy(my,other)) {
                chatRoom = chatRoomRepository.findByMyAndOtherOrOtherAndMy(my, other);
                chatRoom.setTradeBoard(tradeBoard);
                chatRoomRepository.save(chatRoom);


        } else {
            chatRoom.setMy(my);
            chatRoom.setOther(other);
            chatRoom.setTradeBoard(tradeBoard);
            chatRoomRepository.save(chatRoom);
        }
    }


}
