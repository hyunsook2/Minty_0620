package com.Reboot.Minty.tradeBoard.repository;

import com.Reboot.Minty.categories.entity.SubCategory;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TradeBoardRepository extends JpaRepository<TradeBoard,Long> {
//    Page<TradeBoard> findBySubCategory(Optional<SubCategory> subCategory, Pageable pageable);

    Page<TradeBoard> findAllByBoardType(int boardType, Pageable pageable);

    Page<TradeBoard> getBoardsByBoardTypeAndSubCategory(int boardType, Optional<SubCategory> subCategory,  Pageable pageable);

}
