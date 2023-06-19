package com.Reboot.Minty.trade.service;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.trade.repository.TradeRepository;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import com.Reboot.Minty.tradeBoard.repository.TradeBoardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TradeService {
    private final TradeRepository tradeRepository;
    private final TradeBoardRepository tradeBoardRepository;

    @Autowired
    public TradeService(TradeRepository tradeRepository, TradeBoardRepository tradeBoardRepository) {
        this.tradeRepository = tradeRepository;
        this.tradeBoardRepository = tradeBoardRepository;
    }

    public Page<Trade> getList(User user, Pageable pageable) {
        return tradeRepository.findAllByBuyerIdOrSellerId(user, user, pageable);
    }

    public Trade getTradeDetail(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId).orElse(null);
        if (trade == null) {
            // Trade가 존재하지 않는 경우 처리할 로직을 작성하세요.
        }

        return trade;
    }

    public Trade getReviewDetail(Long boardId) {
        Trade trade = tradeRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        return trade;
    }


    public Trade save(TradeBoard tradeBoard, User buyer, User seller) {
        tradeBoard = tradeBoardRepository.save(tradeBoard);
        Trade existingTrades = tradeRepository.findByBoardIdAndBuyerIdAndSellerId(tradeBoard, buyer, seller);
        if(existingTrades != null){
            throw new IllegalStateException(String.valueOf(existingTrades.getId()));
        }
        else {
            Trade trade = new Trade();
            trade.setBoardId(tradeBoard);
            trade.setBuyerId(buyer);
            trade.setSellerId(seller);
            trade.setStatus("대화요청");
            trade.setSellerCheck("N");
            trade.setBuyerCheck("N");
            trade.setStartDate(LocalDateTime.now());
            return tradeRepository.save(trade);
        }
    }

    public String getRoleForTrade(Long tradeId, Long userId) {
        Trade trade = tradeRepository.findById(tradeId).orElse(null);
        if (trade == null) {
            // Trade가 존재하지 않는 경우 처리할 로직을 작성하세요.
        }

        String role = "";
        if (userId.equals(trade.getBuyerId().getId())) {
            role = "buyer";
        } else if (userId.equals(trade.getSellerId().getId())) {
            role = "seller";
        } else {
            // userId가 해당 거래의 구매자나 판매자가 아닌 경우 처리할 로직을 작성하세요.
        }

        return role;
    }

    public List<Trade> getTradeList(Long userId) {
        return tradeRepository.getTradeList(userId);
    }

    public List<User> getTradeUsers(List<Trade> trades, Long userId) {
        List<User> users = new ArrayList<>();
        for (Trade trade : trades) {
            User user = trade.getBuyerId().getId().equals(userId) ? trade.getSellerId() : trade.getBuyerId();
            users.add(user);
        }
        return users;
    }
    public Trade getTradeById(Long tradeId) {
        return tradeRepository.findById(tradeId).orElse(null);
    }

    public void updateStatus(Long tradeId, int statusIndex) {
        String[] statuses = {"대화요청", "거래시작", "거래중", "거래완료", "거래취소"};

        if (statusIndex >= 0 && statusIndex < statuses.length) {
            String newStatus = statuses[statusIndex];

            tradeRepository.updateStatusById(tradeId, newStatus);

            System.out.println("Trade " + tradeId + "의 상태가 " + newStatus + "로 변경되었습니다.");
        } else {
            System.out.println("Invalid status index.");
        }
    }

    public void updateMode(Long tradeId, int modeIndex) {
        String[] modes = {"직거래", "안전거래"};

        if (modeIndex >= 0 && modeIndex < modes.length) {
            String newMode = modes[modeIndex];

            tradeRepository.updateModeById(tradeId, newMode);

            System.out.println("Trade " + tradeId + "의 모드가 " + newMode + "로 변경되었습니다.");
        } else {
            System.out.println("Invalid status index.");
        }
    }

    public void updateTradeSchedule(Long tradeId, LocalDate tradeDate, LocalTime tradeTime) {
        tradeRepository.updateScheduleInfo(tradeId, tradeDate, tradeTime);
    }

    public Trade findTradeById(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId).orElse(null);
        return trade;
    }

    public void saveTrade(Trade trade) {
        tradeRepository.save(trade);
    }



}
