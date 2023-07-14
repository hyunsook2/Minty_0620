package com.Reboot.Minty.emoji.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmojiShopDTO {
    private String name;
    private int price;
    private List<String> images = new ArrayList<>();

}