package com.Reboot.Minty.member.controller;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import com.Reboot.Minty.tradeBoard.repository.TradeBoardRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.Reboot.Minty.member.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    private final TradeBoardRepository tradeBoardRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    public CartController(CartService cartService, TradeBoardRepository tradeBoardRepository, UserRepository userRepository) {
        this.cartService = cartService;
        this.tradeBoardRepository = tradeBoardRepository;
        this.userRepository = userRepository;
    }

    // 찜하기 버튼 클릭 시 처리하는 컨트롤러
    @PostMapping("/incrementInterestingCount/{id}")
    public ResponseEntity<?> incrementInterestingCount(@PathVariable("id") Long id, HttpSession session ) {
        logger.info("incrementInterestingCount method called with id: " + id);

        Long userId =(Long) session.getAttribute("userId");

        TradeBoard tradeBoard = tradeBoardRepository.findById(id).orElseThrow(EntityExistsException::new);
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);


        cartService.saveCart(tradeBoard, user);

        // id를 기반으로 interesting 카운트를 증가시키는 서비스 메서드 호출
        cartService.incrementInterestingCount(id);

        // 뷰로 연결하는 경로 반환 (적절한 뷰 경로로 수정해야 함)
        return new ResponseEntity<>("redirect:/",HttpStatus.OK);
    }
}

