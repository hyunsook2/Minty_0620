package com.Reboot.Minty.trade.entity;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter @Setter
@Entity
@Table(name="Trade")
@ToString
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "board_id")
    private TradeBoard boardId;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User sellerId;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyerId;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "trade_date")
    private LocalDate tradeDate;

    @Column(name = "trade_time")
    private LocalTime tradeTime;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "mode")
    private String mode;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT '대화요청'")
    private String status;

    @Column(name = "seller_check", columnDefinition = "VARCHAR(1) DEFAULT 'N'", nullable = false)
    private String sellerCheck;

    @Column(name = "buyer_check", columnDefinition = "VARCHAR(1) DEFAULT 'N'", nullable = false)
    private String buyerCheck;

    @Column(name = "seller_schedule", columnDefinition = "VARCHAR(1) DEFAULT 'N'", nullable = false)
    private String sellerSchedule;

    @Column(name = "buyer_schedule", columnDefinition = "VARCHAR(1) DEFAULT 'N'", nullable = false)
    private String buyerSchedule;

    @Column
    private String tradeLocation;
}
