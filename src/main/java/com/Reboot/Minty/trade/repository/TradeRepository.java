package com.Reboot.Minty.trade.repository;


import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.tradeBoard.constant.TradeStatus;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    Trade findByBoardId(Long boardId);

    List<Trade> findByBuyerId_Id(Long userId);
    List<Trade> findBySellerId_Id(Long userId);

    Page<Trade> findAllByBuyerIdOrSellerId(User buyer, User seller, Pageable pageable);

    Trade findByBoardIdAndBuyerIdAndSellerId(TradeBoard tradeBoard, User buyer,  User seller);

    @Query("SELECT t FROM Trade t WHERE t.sellerId.id = :userId OR t.buyerId.id = :userId")
    List<Trade> getTradeList(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Trade t SET t.status = :status WHERE t.id = :tradeId")
    void updateStatusById(@Param("tradeId") Long tradeId, @Param("status") String status);

    @Modifying
    @Query("UPDATE Trade t SET t.mode = :mode WHERE t.id = :tradeId")
    void updateModeById(@Param("tradeId") Long tradeId, @Param("mode") String mode);

    @Modifying
    @Query("UPDATE Trade t SET t.tradeDate = :tradeDate, t.tradeTime = :tradeTime WHERE t.id = :tradeId")
    void updateScheduleInfo(@Param("tradeId") Long tradeId, @Param("tradeDate") LocalDate tradeDate, @Param("tradeTime") LocalTime tradeTime);

    @Modifying
    @Query("UPDATE Trade t SET t.sellerSchedule = :schedule WHERE t.id = :tradeId")
    void updateSellerSchedule(@Param("tradeId") Long tradeId, @Param("schedule") String schedule);

    @Modifying
    @Query("UPDATE Trade t SET t.buyerSchedule = :schedule WHERE t.id = :tradeId")
    void updateBuyerSchedule(@Param("tradeId") Long tradeId, @Param("schedule") String schedule);

    List<Trade> findAllByTradeDateBeforeAndStatus(LocalDate tradeDate, String status);

    Trade findByBoardIdAndBuyerId(TradeBoard tradeBoardId, User buyer);
    int countByStatusAndSellerIdOrStatusAndBuyerId(String status1, User seller, String status2, User buyer);


}