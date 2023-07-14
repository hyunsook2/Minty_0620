package com.Reboot.Minty.member.service;


import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.entity.WishlistItem;
import com.Reboot.Minty.member.repository.BoardRepository;
import com.Reboot.Minty.member.repository.WishlistItemRepository;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final BoardRepository boardRepository;
    private final WishlistItemRepository wishlistItemRepository;


    @Autowired
    public CartService(BoardRepository boardRepository, WishlistItemRepository wishlistItemRepository) {
        this.boardRepository = boardRepository;
        this.wishlistItemRepository = wishlistItemRepository;
    }

    @Transactional
    public void incrementInterestingCount(Long id) {
        TradeBoard tradeBoard = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TradeBoard not found"));

        tradeBoard.setInteresting(tradeBoard.getInteresting() + 1);
        boardRepository.save(tradeBoard);

    }

    @Transactional
    public void saveCart (TradeBoard tradeBoard, User user){

        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setUser(user);
        wishlistItem.setTradeBoard(tradeBoard);
        wishlistItemRepository.save(wishlistItem);

    }
}