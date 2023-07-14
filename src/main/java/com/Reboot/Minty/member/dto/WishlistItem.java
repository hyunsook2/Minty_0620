package com.Reboot.Minty.member.entity;

import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wishlist_item")
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne
    @JoinColumn(name = "trade_board_id", nullable = false)
    private TradeBoard tradeBoard;

    // 생성자, 게터, 세터 등의 메서드들...

    // 기타 필요한 메서드들...
}