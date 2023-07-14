package com.Reboot.Minty.emoji.repository;


import com.Reboot.Minty.emoji.entity.EmojiShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmojiShopRepository extends JpaRepository<EmojiShop, Long> {

}
