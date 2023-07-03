package com.Reboot.Minty.tradeBoard.dto;

import com.Reboot.Minty.tradeBoard.constant.TradeStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class TradeBoardDto {
    private Long id;
    private int price;
    private String title;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
    private int interesting;
    private int visit_count;
    private String thumbnail;

    private TopCategory topCategory;
    private SubCategory subCategory;


    private User user;
    private String sellArea;
    private TradeStatus status;

    @QueryProjection
    public TradeBoardDto(Long id, int price, String title,
                         Timestamp createdDate, Timestamp modifiedDate, int interesting,
                         int visit_count, String thumbnail, Long topCategoryId, String topCategoryName,
                         Long subCategoryId, String subCategoryName, Long userId, String userEmail,
                         String userNickName, String sellArea,
                         TradeStatus status) {
        this.id = id;
        this.price = price;
        this.title = title;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.interesting = interesting;
        this.visit_count = visit_count;
        this.thumbnail = thumbnail;
        this.topCategory = new TopCategory(topCategoryId, topCategoryName);
        this.subCategory = new SubCategory(subCategoryId, subCategoryName,
                new TopCategory(topCategoryId, topCategoryName));
        this.user = new User(userId, userEmail, userNickName);
        this.sellArea= sellArea;
        this.status = status;
    }

    public TradeBoardDto(TradeBoardDto v) {
        this.id = v.getId();
        this.price = v.getPrice();
        this.title = v.getTitle();
        this.createdDate = v.getCreatedDate();
        this.modifiedDate = v.getModifiedDate();
        this.interesting = v.getInteresting();
        this.visit_count = v.getVisit_count();
        this.thumbnail = v.getThumbnail();
        this.topCategory = v.getTopCategory();
        this.subCategory = v.getSubCategory();
        this.user = v.getUser();
        this.sellArea = v.getSellArea();
        this.status = v.getStatus();
    }


    // Nested DTO classes with constructors
    @Getter
    @Setter
    @NoArgsConstructor
    public static class TopCategory {
        private Long id;
        private String name;

        public TopCategory(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SubCategory {
        private Long id;
        private String name;
        private TopCategory topCategory;

        public SubCategory(Long id, String name, TopCategory topCategory) {
            this.id = id;
            this.name = name;
            this.topCategory = topCategory;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class User {
        private Long id;
        private String email;
        private String nickName;

        public User(Long id, String email, String nickName) {
            this.id = id;
            this.email = email;
            this.nickName = nickName;
        }
    }

}

