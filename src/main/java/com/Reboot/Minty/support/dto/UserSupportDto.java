package com.Reboot.Minty.support.dto;

import com.Reboot.Minty.support.entity.UserSupport;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserSupportDto {

    private Long id;
    @NotEmpty(message = "필수")
    private String title;

    @NotEmpty(message = "필수")
    private String content;
    private String name;
    private String verifyReply;
    private String nickname;
    private List<FileDto> files = new ArrayList<>();
    private List<String> filePaths = new ArrayList<>();
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Long userId;

    public UserSupport toEntity() {
        return UserSupport.builder()
                .id(id)
                .title(title)
                .name(name)
                .content(content)
                .nickname(nickname)
                .userId(userId)
                .verifyReply(verifyReply)
                .build();
    }

    @Builder
    public UserSupportDto(Long id, String title, String content, String name,
                    String verifyReply, String nickname, LocalDateTime createdDate, LocalDateTime modifiedDate
                    ,Long userId,List<FileDto> files, List<String> filePaths) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.name = name;
        this.nickname = nickname;
        this.verifyReply = verifyReply;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.userId = userId;
        this.files =files;
        this.filePaths = filePaths;
    }

}
