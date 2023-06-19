package com.Reboot.Minty.review.dto;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;


@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private String imageUrl; // 이미지 파일 URL 추가
    private String contents;
    private int rating;
    private MultipartFile imageFile;
    private String nickname;
    private User writerId;
    private User receiverId;
    private LocalDateTime writeTime;
    private Trade tradeId;

}