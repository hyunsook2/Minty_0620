package com.Reboot.Minty.review.service;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.review.dto.ReviewDto;
import com.Reboot.Minty.review.entity.Review;
import com.Reboot.Minty.review.repository.ReviewRepository;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.trade.repository.TradeRepository;
import com.Reboot.Minty.trade.service.TradeService;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private UserRepository userRepository;
    private TradeRepository tradeRepository;
    private TradeService tradeService;
    private UserService userService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,UserRepository userRepository, UserService userService, TradeRepository tradeRepository,TradeService tradeService) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.tradeRepository = tradeRepository;
        this.tradeService = tradeService;
        this.userService = userService;

    }

    //내가 작성한 후기
    public List<Review> getReviewsByWriterIdOrderByWriteTimeDesc(User writerId) {
        return reviewRepository.getReviewsByWriterIdOrderByWriteTimeDesc(writerId);
    }

    // 내가 받은 후기
    public List<Review> getReceivedReviewsByReceiverIdOrderByWriteTimeDesc(User receiverId) {
        return reviewRepository.getReceivedReviewsByReceiverIdOrderByWriteTimeDesc(receiverId);
    }


    // 개인상점 후기 (남꺼)
    public List<Review> getReviewsByReceiverId(Long receiverId) {
        return reviewRepository.findByReceiverId(receiverId);
    }
    public List<ReviewDto> getAllReviews() {
        List<Review> reviews = reviewRepository.findAllByOrderByWriteTimeDesc();
        List<ReviewDto> reviewDtos = new ArrayList<>();

        for (Review review : reviews) {
            ReviewDto reviewDto = convertToDto(review);
            reviewDtos.add(reviewDto);
        }

        return reviewDtos;
    }

    public void createReview(ReviewDto reviewDto) {
        System.out.println("Creating review: " + reviewDto.getContents());
        System.out.println("Creating review: " + reviewDto.getRating());
        System.out.println("Creating review: " + reviewDto.getWriterId());
        System.out.println("Creating review: " + reviewDto.getReceiverId());
        System.out.println("Creating review: " + reviewDto.getImageUrl());
        System.out.println("Creating review: " + reviewDto.getNickname());
        System.out.println("Creating review: " + reviewDto.getId());
        System.out.println("Creating review: " + reviewDto.getTradeId());

        Review review = convertToEntity(reviewDto);


        Trade trade = reviewDto.getTradeId();
        User user = reviewDto.getWriterId();

        User receiver = findReceiverId(trade, user);

        if (trade.getSellerId().equals(user.getId())) {
            reviewDto.setWriterId(user);
            reviewDto.setReceiverId(trade.getBuyerId());
        } else if (trade.getBuyerId().equals(user.getId())) {
            reviewDto.setWriterId(user);
            reviewDto.setReceiverId(trade.getSellerId());
        } else {
            // 예외 처리 로직 (거래 참여자가 아닌 경우)
        }

        reviewDto.setReceiverId(receiver);


        LocalDateTime currentTime = LocalDateTime.now();
        review.setWriteTime(currentTime);

        // 리뷰를 리포지토리에 저장합니다
        review = reviewRepository.save(review);

        // 이미지 파일을 저장하고 이미지 파일의 경로를 엔티티에 저장합니다
        String imageUrl = storeImageFile(review.getId(), reviewDto.getImageFile());
        review.setImageUrl(imageUrl);

        // 리뷰를 업데이트합니다 (이미지 파일 경로를 업데이트하기 위해)
        reviewRepository.save(review);
    }
    public User findReceiverId(Trade trade, User user) {
        User receiver;
        if (trade.getSellerId().getId().equals(user.getId())) {
            receiver = trade.getBuyerId();
        } else if (trade.getBuyerId().getId().equals(user.getId())) {
            receiver = trade.getSellerId();
        } else {
            // 예외 처리: 작성자가 거래 참여자가 아닌 경우
            throw new IllegalStateException("Writer is not a participant in the trade");
        }
        return receiver;
    }



    private Review convertToEntity(ReviewDto reviewDto) {

        return Review.builder()
                .contents(reviewDto.getContents())
                .rating(reviewDto.getRating())
                .writeTime(reviewDto.getWriteTime())
                .receiverId(reviewDto.getReceiverId())
                .writerId(reviewDto.getWriterId())
                .nickname(reviewDto.getNickname())
                .tradeId(reviewDto.getTradeId())
                .build();
    }


    private ReviewDto convertToDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setId(review.getId());
        reviewDto.setContents(review.getContents());
        reviewDto.setNickname(review.getNickname());
        reviewDto.setWriteTime(review.getWriteTime());
        reviewDto.setRating(review.getRating());
        reviewDto.setReceiverId(review.getReceiverId());
        reviewDto.setWriterId(review.getWriterId());
        reviewDto.setTradeId(review.getTradeId());

        // 이미지 파일의 URL을 설정합니다
        String imageUrl = review.getImageUrl(); // 이미지 파일의 경로를 가져옴
        reviewDto.setImageUrl(imageUrl);

        return reviewDto;
    }

    private String storeImageFile(Long reviewId, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        try {
            // 이미지 파일을 저장할 디렉토리를 정의합니다
            String storageDirectory = "D:/intellijPrac/Minty/src/main/resources/static/image/review/";
            File directory = new File(storageDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 리뷰 ID를 사용하여 고유한 파일명을 생성합니다
            String originalFilename = imageFile.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String uniqueFilename = reviewId + fileExtension;

            // 이미지 파일을 저장 디렉토리에 저장합니다
            File file = new File(storageDirectory + uniqueFilename);
            imageFile.transferTo(file);

            // 저장된 파일 경로를 반환합니다
            return "/image/review/" + uniqueFilename;
        } catch (IOException e) {
            // 파일 저장 중에 오류가 발생한 경우 예외를 처리합니다
            e.printStackTrace();
            return null;
        }
    }

    public void deleteReview(Long id) {
        // 리포지토리에서 리뷰를 가져옵니다
        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null) {
            // 리뷰가 존재하지 않는 경우 처리합니다
            return;
        }

        // 리포지토리에서 리뷰를 삭제합니다
        reviewRepository.delete(review);
    }


    public boolean existsByIdAndWriterId(Trade tradeId, User writerId){
        return reviewRepository.existsByTradeIdAndWriterId(tradeId, writerId);
    }

    public Review getReviewByTradeIdAndWriterId(Trade tradeId, User writerId) {
        Optional<Review> optionalReview = reviewRepository.findByTradeIdAndWriterId(tradeId, writerId);
        return optionalReview.orElse(null);
    }

    //평점
    public double calculateAverageRating(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return 0.0;
        }

        int totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }

        return (double) totalRating / reviews.size();
    }
}