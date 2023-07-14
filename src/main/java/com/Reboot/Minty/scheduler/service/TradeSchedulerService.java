package com.Reboot.Minty.scheduler.service;

import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.trade.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@Service
@EnableScheduling
public class TradeSchedulerService {
    @Autowired
    private final TradeRepository tradeRepository;

    public TradeSchedulerService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @PostConstruct
    public void init() {
        updateTradeStatus();
    }

    // 매일 정각에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void updateTradeStatus() {
        // 현재 시간보다 3일 이전의 날짜를 조회
        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
        List<Trade> trades = tradeRepository.findAllByTradeDateBeforeAndStatus(threeDaysAgo, "거래중");

        // 상태를 "거래완료"로 변경
        for (Trade trade : trades) {
            trade.setStatus("거래완료");
        }

        // 변경된 상태를 저장
        tradeRepository.saveAll(trades);
    }
}
