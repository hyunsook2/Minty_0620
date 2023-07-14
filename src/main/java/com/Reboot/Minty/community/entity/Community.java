package com.Reboot.Minty.community.entity;

import com.Reboot.Minty.community.constant.BoardStatus;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.tradeBoard.constant.TradeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.sql.Timestamp;

@Entity
@Table(name = "community")
@Getter
@Setter
@DynamicInsert
public class Community {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(name= "created_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdDate;

    @Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp modifiedDate;

    @Column(nullable = false)
    private int interesting;

    @Column(columnDefinition = "INT DEFAULT 0", nullable = false)
    private int visitCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private BoardStatus status;

}
