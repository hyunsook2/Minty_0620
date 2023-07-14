package com.Reboot.Minty.tradeBoard.repository;

import com.Reboot.Minty.tradeBoard.entity.TradeBoardImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeBoardImgRepository extends JpaRepository<TradeBoardImg,Long> {

    List<TradeBoardImg> findByTradeBoardId(Long boardId);
}
