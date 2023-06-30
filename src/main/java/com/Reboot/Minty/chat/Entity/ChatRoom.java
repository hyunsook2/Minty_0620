package com.Reboot.Minty.chat.Entity;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="chatRoom")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "my")
    private User my;

    @ManyToOne
    @JoinColumn(name = "other")
    private User other;

    @ManyToOne
    @JoinColumn(name = "tradeBoardId")
    private TradeBoard tradeBoard;



}
