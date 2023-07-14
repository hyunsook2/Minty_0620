package com.Reboot.Minty.tradeBoard.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TradeBoardDetailResponseDto {
    private boolean isAuthor;
    private String nickName;
    private TradeBoardDetailDto tradeBoard;
    private List<TradeBoardImgDto> imageList;
    private boolean wish;
}
