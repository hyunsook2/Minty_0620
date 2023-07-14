package com.Reboot.Minty.tradeBoard.repository;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import com.Reboot.Minty.tradeBoard.entity.WishLike;
import com.oracle.wls.shaded.org.apache.bcel.generic.LNEG;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishLikeRepository extends JpaRepository<WishLike,Long> {

    Optional<WishLike> findByTradeBoardAndUser(TradeBoard tradeBoard, User user);

    WishLike findByUserAndTradeBoard(User user, TradeBoard boardId);
}
