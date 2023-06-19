package com.Reboot.Minty.review.entity;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "writer_id", nullable = false)
    private User writerId;

    @Column(name = "contents", nullable = false)
    private String contents;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "image_url") // 이미지 파일 URL 저장하는 필드 추가
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiverId;

    @Column(name = "nickname")
    private String nickname;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "trade_id")
    private Trade tradeId;

    @Column(name = "write_time")
    private LocalDateTime writeTime;

    public Review() {
    }
}
