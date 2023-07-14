package com.Reboot.Minty.tradeBoard.entity;

import com.Reboot.Minty.tradeBoard.dto.TradeBoardDto;
import com.Reboot.Minty.tradeBoard.dto.TradeBoardImgDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.modelmapper.ModelMapper;


@Entity
@Data
public class TradeBoardImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tradeBoard_id")
    private TradeBoard tradeBoard;

    @Column(name="img_url")
    private String imgUrl;

    private static ModelMapper modelMapper =  new ModelMapper();

    public static TradeBoardImgDto toDto(TradeBoardImg tradeBoardImg) {
        return modelMapper.map(tradeBoardImg, TradeBoardImgDto.class);
    }
}
