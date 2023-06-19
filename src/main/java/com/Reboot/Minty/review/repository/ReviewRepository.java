package com.Reboot.Minty.review.repository;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.review.entity.Review;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import jakarta.persistence.OrderBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
        List<Review> findAllByOrderByWriteTimeDesc();
        boolean existsByTradeIdAndWriterId(Trade tradeId, User writerId);
        List<Review> getReviewsByWriterIdOrderByWriteTimeDesc(User writerId);

        List<Review> getReceivedReviewsByReceiverIdOrderByWriteTimeDesc(User receiverId);
        List<Review> findByReceiverId(Long receiverId);

        Optional<Review> findByTradeIdAndWriterId(Trade tradeId, User writerId);

}
