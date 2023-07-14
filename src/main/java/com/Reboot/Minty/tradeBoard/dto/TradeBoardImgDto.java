package com.Reboot.Minty.tradeBoard.dto;

import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import com.Reboot.Minty.tradeBoard.entity.TradeBoardImg;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@NoArgsConstructor
public class TradeBoardImgDto {
    private Long id;
    private TradeBoardDetailDto tradeBoard;
    private String imgUrl;

    private static ModelMapper modelMapper = new ModelMapper();

    public static TradeBoardImgDto of(TradeBoardImg tradeBoardImg){ return modelMapper.map(tradeBoardImg,TradeBoardImgDto.class);}
}
