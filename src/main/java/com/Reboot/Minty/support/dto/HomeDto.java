package com.Reboot.Minty.support.dto;

import com.Reboot.Minty.support.entity.Home;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class HomeDto {
    private Long id;
    private String title;
    private String content;

    public Home toEntity(){
        return Home.builder()
                .id(id)
                .title(title)
                .content(content)
                .build();
    }

    @Builder
    public HomeDto(Long id, String title, String content){
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
