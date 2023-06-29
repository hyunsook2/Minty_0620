package com.Reboot.Minty.tradeBoard.dto;

import com.Reboot.Minty.categories.entity.SubCategory;
import com.Reboot.Minty.categories.entity.TopCategory;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.entity.UserLocation;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class TradeBoardFormDto {

    @NotBlank(message = "제목은 필수 입니다.")
    private String title;
    @NotBlank(message = "내용은 필수 입니다.")
    private String content;

    @Positive(message = "양수만 입력해주세요.")
    @NotNull(message = "가격은 필수입니다.")
    private Integer price;

    private TopCategory topCategory;

    @NotNull(message="카테고리는 필수 선택 사항입니다.")
    private SubCategory subCategory;

    private User user;
    private int boardType;
    private UserLocation userLocation;

    private static ModelMapper modelMapper =  new ModelMapper();
    public static TradeBoard toEntity(TradeBoardFormDto dto) {
        return modelMapper.map(dto, TradeBoard.class);
    }

    public void updateEntity(TradeBoard tradeBoard) {
        modelMapper.map(this, tradeBoard);
    }
}