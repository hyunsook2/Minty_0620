package com.Reboot.Minty.tradeBoard.entity;



import com.Reboot.Minty.categories.entity.SubCategory;
import com.Reboot.Minty.categories.entity.TopCategory;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.entity.UserLocation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import java.sql.Timestamp;

@Entity
@Table(name = "tradeboard")
@Getter
@Setter
@DynamicInsert
public class TradeBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;

    @Column(name= "created_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdDate;

    @Column(nullable = false)
    private int interesting;

    @Column(columnDefinition = "INT DEFAULT 0", nullable = false)
    private int visit_count;

    @ManyToOne
    @JoinColumn(name= "top_category_id", nullable = false)
    private TopCategory topCategory;

    @ManyToOne
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategory subCategory;


    private String thumbnail;

    @Column(name="board_type", nullable = false)
    private int boardType;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="user_location")
    private UserLocation userLocation;

    @Column(columnDefinition = "INT DEFAULT 0", nullable = false)
    private int status;

}