package com.Reboot.Minty.chat.repository;

import com.Reboot.Minty.chat.Entity.Products;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Products, Long> {

    boolean existsByMyAndOtherAndTrade(User my, User other, Trade trade);
}
