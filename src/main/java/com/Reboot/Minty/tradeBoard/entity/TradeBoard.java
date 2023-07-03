package com.Reboot.Minty.tradeBoard.entity;



import com.Reboot.Minty.categories.entity.SubCategory;
import com.Reboot.Minty.categories.entity.TopCategory;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.entity.UserLocation;
import com.Reboot.Minty.tradeBoard.constant.TradeStatus;
import com.Reboot.Minty.tradeBoard.dto.TradeBoardDetailDto;
import com.Reboot.Minty.tradeBoard.dto.TradeBoardDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.modelmapper.ModelMapper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, length = 10000)
    private String content;


    @Column(name= "created_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdDate;

    @Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp modifiedDate;

    @Column(nullable = false)
    private int interesting;

    @Column(columnDefinition = "INT DEFAULT 0", nullable = false)
    private int visit_count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "top_category_id", nullable = false)
    private TopCategory topCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategory subCategory;

    private String thumbnail;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @Column(nullable = false)
    private String sellArea;

    @Enumerated(EnumType.STRING)
    private TradeStatus status;

    @OneToMany(mappedBy = "tradeBoard" , cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TradeBoardImg> images = new ArrayList<>();

    private static ModelMapper modelMapper =  new ModelMapper();

    public static TradeBoardDetailDto toDto(TradeBoard tradeBoard) {
        TradeBoardDetailDto dto = modelMapper.map(tradeBoard, TradeBoardDetailDto.class);
        return dto;
    }


}