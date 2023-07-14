package com.Reboot.Minty.emoji.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
public class EmojiShop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
    private String description;
    @ElementCollection
    private List<String> images = new ArrayList<>(); // 이미지 파일의 경로를 저장하는 리스트

}
