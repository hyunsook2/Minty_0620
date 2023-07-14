package com.Reboot.Minty.emoji.service;


import com.Reboot.Minty.emoji.entity.EmojiShop;
import com.Reboot.Minty.emoji.repository.EmojiShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmojiShopService {

    private EmojiShopRepository emojiShopRepository;

    public EmojiShopService(EmojiShopRepository emojiShopRepository) {
        this.emojiShopRepository = emojiShopRepository;
    }
    public List<EmojiShop> getAllEmojiShops() {
        return emojiShopRepository.findAll();
    }
    public EmojiShop getEmojiShopById(Long id) {
        return emojiShopRepository.findById(id).orElse(null);
    }


    // 06.22 추가
    public void saveEmoji(EmojiShop emojiShop){ emojiShopRepository.save(emojiShop);}
}






