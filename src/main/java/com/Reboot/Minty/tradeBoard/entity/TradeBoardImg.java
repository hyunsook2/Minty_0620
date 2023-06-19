package com.Reboot.Minty.tradeBoard.entity;

import jakarta.persistence.*;
import lombok.Data;



@Entity
@Data
public class TradeBoardImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="trade_board_id")
    private TradeBoard tradeBoard;

    @Column(name="img_url")
    private String imgUrl;
}
