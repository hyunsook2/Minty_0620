package com.Reboot.Minty.tradeBoard.repository;

import com.Reboot.Minty.categories.entity.SubCategory;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeBoardRepository extends JpaRepository<TradeBoard,Long> {
    List<TradeBoard> findByUserId(Long userId);
}