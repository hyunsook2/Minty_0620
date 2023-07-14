package com.Reboot.Minty.member.repository;


import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<TradeBoard, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE TradeBoard b SET b.interesting = b.interesting + 1 WHERE b.id = :id")
    void incrementInterestingCount(Long id);
}