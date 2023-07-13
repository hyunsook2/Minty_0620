package com.Reboot.Minty.trade.service;

import com.Reboot.Minty.manager.entity.ManagerStatistics;
import com.Reboot.Minty.manager.repository.ManagerStatisticsRepository;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.trade.repository.TradeRepository;
import com.Reboot.Minty.tradeBoard.constant.TradeStatus;
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
    private final ManagerStatisticsRepository managerStatisticsRepository;
    private final UserRepository userRepository;

    @Autowired
    public TradeService(TradeRepository tradeRepository, TradeBoardRepository tradeBoardRepository, ManagerStatisticsRepository managerStatisticsRepository, UserRepository userRepository) {
        this.tradeRepository = tradeRepository;
        this.tradeBoardRepository = tradeBoardRepository;
        this.managerStatisticsRepository = managerStatisticsRepository;
        this.userRepository = userRepository;
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
            trade.setMode("직거래");
            trade.setStatus("대화요청");
            trade.setSellerCheck("N");
            trade.setBuyerCheck("N");
            trade.setSellerSchedule("N");
            trade.setBuyerSchedule("N");
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

        Trade trade = tradeRepository.findById(tradeId).orElseThrow(EntityNotFoundException::new);
        TradeBoard tradeBoard = trade.getBoardId();

        if (statusIndex >= 0 && statusIndex < statuses.length) {
            String newStatus = statuses[statusIndex];
            tradeRepository.updateStatusById(tradeId, newStatus);

            TradeStatus newTradeStatus = null;
            if (statusIndex == 1) {
                newTradeStatus = TradeStatus.TRADING;
            } else if (statusIndex == 3) {
                newTradeStatus = TradeStatus.SOLD_OUT;
            } else if (statusIndex == 4) {
                newTradeStatus = TradeStatus.SELL;

                // Reset other values for trade cancellation
                trade.setMode("직거래");
                trade.setSellerCheck("N");
                trade.setBuyerCheck("N");
                trade.setSellerSchedule("N");
                trade.setBuyerSchedule("N");
                tradeRepository.save(trade);
            }

            if(newTradeStatus != null){
                tradeBoard.setStatus(newTradeStatus);
                tradeBoardRepository.save(tradeBoard);
                System.out.println("TradeBoard " + tradeBoard.getId() + "의 TradeStatus가 " + newTradeStatus + "로 변경되었습니다.");
            }

            System.out.println("Trade " + tradeId + "의 상태가 " + newStatus + "로 변경되었습니다.");
        } else {
            System.out.println("Invalid status index.");
        }
    }

    public String updateMode(Long tradeId, int modeIndex, User buyer, User seller) {
        String[] modes = {"직거래", "안전거래"};
        String errorMessage = null;

        if (modeIndex >= 0 && modeIndex < modes.length) {
            String newMode = modes[modeIndex];

            Trade trade = tradeRepository.findById(tradeId).orElseThrow(() -> new RuntimeException("Trade not found"));
            TradeBoard tradeBoard = trade.getBoardId();

            if (newMode.equals("안전거래")) {
                int requiredSellerBalance = (int)(tradeBoard.getPrice() * 0.10); // calculate 10% of the price for seller
                int requiredBuyerBalance = tradeBoard.getPrice(); // calculate the total price for buyer

                if (buyer.getBalance() < requiredBuyerBalance) {
                    errorMessage = "구매자님의 잔고가 충분하지 않습니다.";
                }

                if (seller.getBalance() < requiredSellerBalance) {
                    if (errorMessage == null) {
                        errorMessage = "판매자님의 잔고가 충분하지 않습니다.";
                    } else {
                        errorMessage += " 그리고 판매자님의 잔고도 충분하지 않습니다.";
                    }
                }

                if (errorMessage != null) {
                    return errorMessage;
                }
            }

            tradeRepository.updateModeById(tradeId, newMode);
            System.out.println("Trade " + tradeId + "의 모드가 " + newMode + "로 변경되었습니다.");
        } else {
            System.out.println("Invalid mode index.");
        }

        return errorMessage;
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

    public void updateScheduleCheck(Long tradeId, Long userId) {
        String role = getRoleForTrade(tradeId, userId);
        if (role.equals("seller")) {
            tradeRepository.updateSellerSchedule(tradeId, "Y");
            tradeRepository.updateBuyerSchedule(tradeId, "N");
        } else if (role.equals("buyer")) {
            tradeRepository.updateBuyerSchedule(tradeId, "Y");
            tradeRepository.updateSellerSchedule(tradeId, "N");
        } else {
            // 해당 거래의 구매자나 판매자가 아닌 경우 처리할 로직을 작성하세요.
        }
    }

    @Transactional
    public void confirmationSchedule(Long tradeId, Long userId,String role) {
        Trade trade = tradeRepository.findById(tradeId).orElse(null);
        if (trade == null) {
            // Trade가 존재하지 않는 경우 처리할 로직을 작성하세요.
            return ;
        }
        if (role.equals("seller")) {
            tradeRepository.updateSellerSchedule(tradeId, "Y");
            this.updateStatus(tradeId,2);
        } else if (role.equals("buyer")) {
            tradeRepository.updateBuyerSchedule(tradeId, "Y");
            this.updateStatus(tradeId,2);
        } else {
            // 해당 거래의 구매자나 판매자가 아닌 경우 처리할 로직을 작성하세요.
            return;
        }
    }

    @Transactional
    public void completionTrade(Long tradeId, String role, User buyer, User seller) {
        Trade trade = tradeRepository.findById(tradeId).orElse(null);
        if (trade == null) {
            // Trade가 존재하지 않는 경우 처리할 로직을 작성하세요.
            return;
        }
        if (role.equals("seller")) {
            trade.setSellerCheck("Y");
            if (trade.getBuyerCheck().equals("Y")) {
                trade.setStatus("거래완료");
                trade.setEndDate(LocalDateTime.now());

                if (trade.getMode().equals("안전거래")) {
                    int tradePrice = trade.getBoardId().getPrice();
                    buyer.setBalance(buyer.getBalance() - tradePrice); // buyer's balance is deducted by the trade price
                    seller.setBalance(seller.getBalance() + tradePrice); // seller's balance is increased by the trade price

                    // Make sure to save the updated user balances
                    userRepository.save(buyer);
                    userRepository.save(seller);
                }

                ManagerStatistics managerStatistics = managerStatisticsRepository.findByVisitDate(LocalDate.now());
                if (managerStatistics != null) {
                    int transactionCount = managerStatistics.getTransaction() + 1;
                    managerStatistics.setTransaction(transactionCount);
                    managerStatisticsRepository.save(managerStatistics);
                }
            }
            tradeRepository.save(trade);
        } else if (role.equals("buyer")) {
            trade.setBuyerCheck("Y");
            if (trade.getSellerCheck().equals("Y")) {
                trade.setStatus("거래완료");
                trade.setEndDate(LocalDateTime.now());

                if (trade.getMode().equals("안전거래")) {
                    int tradePrice = trade.getBoardId().getPrice();
                    buyer.setBalance(buyer.getBalance() - tradePrice); // buyer's balance is deducted by the trade price
                    seller.setBalance(seller.getBalance() + tradePrice); // seller's balance is increased by the trade price

                    // Make sure to save the updated user balances
                    userRepository.save(buyer);
                    userRepository.save(seller);
                }

                ManagerStatistics managerStatistics = managerStatisticsRepository.findByVisitDate(LocalDate.now());
                if (managerStatistics != null) {
                    int inquiryCount = managerStatistics.getInquiry() + 1;
                    managerStatistics.setInquiry(inquiryCount);
                    managerStatisticsRepository.save(managerStatistics);
                }
            }
            tradeRepository.save(trade);
        } else {
            // 해당 거래의 구매자나 판매자가 아닌 경우 처리할 로직을 작성하세요.
            return;
        }
    }

    @Transactional
    public void changeTrade(Long tradeId){
        this.updateStatus(tradeId,1);
        Trade trade = tradeRepository.findById(tradeId).orElseThrow(EntityNotFoundException::new);
        trade.setSellerSchedule("N");
        trade.setBuyerSchedule("N");
        trade.setTradeDate(null);
        trade.setTradeTime(null);
        tradeRepository.save(trade);
    }

    public void saveTradeLocation(Long tradeId, String tradeLocation){
        Trade trade = tradeRepository.findById(tradeId).orElseThrow(EntityNotFoundException::new);
        trade.setTradeLocation(tradeLocation);
        tradeRepository.save(trade);
    }
    public Trade findTradeBoardById(TradeBoard tradeBoardId, User buyer) {
        Trade trade = tradeRepository.findByBoardIdAndBuyerId(tradeBoardId,buyer);
        return trade;
    }

    public int getTradeCountForStoreOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId).orElse(null);
        if (owner == null) {
            // 상점 주인을 찾을 수 없는 경우 처리
            return 0;
        }
        return tradeRepository.countByStatusAndSellerIdOrStatusAndBuyerId("거래완료", owner, "거래완료", owner);
    }

}