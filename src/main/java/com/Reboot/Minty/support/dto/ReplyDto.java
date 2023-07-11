package com.Reboot.Minty.support.dto;

import com.Reboot.Minty.support.entity.ReplyEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ReplyDto {
    @NotBlank(message = "필수")
    private String replyTitle;
    @NotBlank(message = "필수")
    private String replyContent;
    private String nickname;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public ReplyEntity toEntity(){
        return ReplyEntity.builder()
                .replyContent(replyContent)
                .replyTitle(replyTitle)
                .nickname(nickname)
                .build();
    }

    @Builder
    public ReplyDto( String replyContent, String replyTitle, String nickname,
                    LocalDateTime createdDate, LocalDateTime modifiedDate){
        this.replyContent = replyContent;
        this.replyTitle = replyTitle;
        this.nickname = nickname;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;

    }
}
