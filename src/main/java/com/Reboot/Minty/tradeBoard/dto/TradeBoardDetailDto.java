package com.Reboot.Minty.tradeBoard.dto;

import com.Reboot.Minty.categories.entity.SubCategory;
import com.Reboot.Minty.categories.entity.TopCategory;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.entity.UserLocation;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.tradeBoard.constant.TradeStatus;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TradeBoardDetailDto {
    private Long id;
    private int price;
    private String title;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
    private int interesting;
    private int visit_count;
    private TopCategoryDto topCategory;
    private SubCategoryDto subCategory;
    private UserDto user;
    private UserLocationDto userLocation;
    private TradeStatus tradeStatus;

    @Getter
    @Setter
    public static class TopCategoryDto{
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    public static class SubCategoryDto{
        private Long id;
        private String name;

        private TopCategoryDto topCategory;
    }

    @Getter
    @Setter
    public static class UserDto{
        private Long id;
        private String nickName;
        private String email;
    }

    @Getter
    @Setter
    public static class UserLocationDto{
        private Long id;
        private String address;
        private UserDto user;
    }


    private static ModelMapper modelMapper = new ModelMapper();

    public static TradeBoardDetailDto of(TradeBoard tradeBoard) {
        TradeBoardDetailDto tradeBoardDetailDto = new TradeBoardDetailDto();
        tradeBoardDetailDto.setId(tradeBoard.getId());
        tradeBoardDetailDto.setPrice(tradeBoard.getPrice());
        tradeBoardDetailDto.setTitle(tradeBoard.getTitle());
        tradeBoardDetailDto.setCreatedDate(tradeBoard.getCreatedDate());
        tradeBoardDetailDto.setModifiedDate(tradeBoard.getModifiedDate());
        tradeBoardDetailDto.setInteresting(tradeBoard.getInteresting());
        tradeBoardDetailDto.setVisit_count(tradeBoard.getVisit_count());

        // Create and populate the TopCategoryDto object
        TopCategoryDto topCategoryDto = new TopCategoryDto();
        TopCategory topCategory = tradeBoard.getTopCategory();
        topCategoryDto.setId(topCategory.getId());
        topCategoryDto.setName(topCategory.getName());
        tradeBoardDetailDto.setTopCategory(topCategoryDto);

        SubCategoryDto subCategoryDto = new SubCategoryDto();
        SubCategory subCategory = tradeBoard.getSubCategory();
        subCategoryDto.setId(subCategory.getId());
        subCategoryDto.setName(subCategory.getName());
        subCategoryDto.setTopCategory(topCategoryDto);
        tradeBoardDetailDto.setSubCategory(subCategoryDto);

        UserDto userDto= new UserDto();
        userDto.setId(tradeBoard.getUser().getId());
        userDto.setNickName(tradeBoard.getUser().getNickName());
        userDto.setEmail(tradeBoard.getUser().getEmail());
        tradeBoardDetailDto.setUser(userDto);

        UserLocationDto userLocationDto = new UserLocationDto();
        userLocationDto.setId(tradeBoard.getUserLocation().getId());
        userLocationDto.setAddress(tradeBoard.getUserLocation().getAddress());
        userLocationDto.setUser(userDto);
        tradeBoardDetailDto.setUserLocation(userLocationDto);

        tradeBoardDetailDto.setTradeStatus(tradeBoard.getStatus());
        return tradeBoardDetailDto;
    }
}
