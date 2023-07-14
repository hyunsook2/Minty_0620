package com.Reboot.Minty.member.repository;

import com.Reboot.Minty.member.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
}