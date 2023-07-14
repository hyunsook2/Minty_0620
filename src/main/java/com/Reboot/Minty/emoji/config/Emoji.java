package com.Reboot.Minty.emoji.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Emoji implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/pointshop/**") // 이미지 파일 요청 경로 설정
                .addResourceLocations("file:D:/intellijPrac/Minty/src/main/resources/static/image/pointshop/"); // 실제 파일의 위치 설정
    }

}
