package com.Reboot.Minty.tradeBoard.service;

import com.Reboot.Minty.config.ResizeFile;
import com.Reboot.Minty.member.dto.UserLocationResponseDto;
import com.Reboot.Minty.member.dto.UserResponseDto;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.entity.UserLocation;
import com.Reboot.Minty.member.repository.UserLocationRepository;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.tradeBoard.constant.TradeStatus;
import com.Reboot.Minty.tradeBoard.dto.*;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import com.Reboot.Minty.tradeBoard.entity.TradeBoardImg;
import com.Reboot.Minty.tradeBoard.entity.WishLike;
import com.Reboot.Minty.tradeBoard.repository.TradeBoardCustomRepository;
import com.Reboot.Minty.tradeBoard.repository.TradeBoardImgRepository;
import com.Reboot.Minty.tradeBoard.repository.TradeBoardRepository;
import com.Reboot.Minty.tradeBoard.repository.WishLikeRepository;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class TradeBoardService {
    private final TradeBoardRepository tradeBoardRepository;

    private final TradeBoardImgRepository tradeBoardImgRepository;

    private final UserRepository userRepository;

    private final UserLocationRepository userLocationRepository;

    private final TradeBoardCustomRepository tradeBoardCustomRepository;

    private final WishLikeRepository wishLikeRepository;


    @Autowired
    private Storage storage;




    @Autowired
    public TradeBoardService(TradeBoardRepository tradeBoardRepository, TradeBoardImgRepository tradeBoardImgRepository, UserRepository userRepository, UserLocationRepository userLocationRepository, TradeBoardCustomRepository tradeBoardCustomRepository, WishLikeRepository wishLikeRepository) {
        this.tradeBoardRepository = tradeBoardRepository;
        this.tradeBoardImgRepository = tradeBoardImgRepository;
        this.userRepository = userRepository;
        this.userLocationRepository = userLocationRepository;
        this.tradeBoardCustomRepository = tradeBoardCustomRepository;
        this.wishLikeRepository = wishLikeRepository;
    }

    public Slice<TradeBoardDto> getTradeBoard(TradeBoardSearchDto tradeBoardSearchDto, Pageable pageable){
        return tradeBoardCustomRepository.getTradeBoardBy(tradeBoardSearchDto, pageable);
    }


    public TradeBoard save(TradeBoard tradeBoard) {
        return tradeBoardRepository.save(tradeBoard);
    }

    public TradeBoardDetailDto findById(Long boardId) {
        TradeBoard tradeBoard = tradeBoardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);

        tradeBoard.setVisit_count(tradeBoard.getVisit_count()+1);
        tradeBoardRepository.save(tradeBoard);

        TradeBoardDetailDto dto = TradeBoardDetailDto.of(tradeBoard);
        System.out.println("of TradeBoardDetailDto" + dto.getTopCategory());
        if(dto.getTradeStatus().equals(TradeStatus.BANNED)||dto.getTradeStatus().equals(TradeStatus.DELETING)){
            throw new AccessDeniedException("해당 글의 접근 권한이 없습니다.");
        }else{
            return dto;
        }
    }

    public List<TradeBoardImgDto> getImgList(Long boardId) {
        List<TradeBoardImg> tradeBoardImg = tradeBoardImgRepository.findByTradeBoardId(boardId);
        List<TradeBoardImgDto> tradeBoardImgDto = tradeBoardImg.stream()
                .map(TradeBoardImgDto::of)
                .collect(Collectors.toList());
        return tradeBoardImgDto;
    }

    @Value("${spring.cloud.gcp.storage.credentials.bucket}")
    private String bucketName;

    public Long saveBoard(Long userId, TradeBoardFormDto tradeBoardFormDto, List<MultipartFile> mf) {
        String uuid = UUID.randomUUID().toString();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        TradeBoard tradeBoard = tradeBoardFormDto.toEntity(tradeBoardFormDto);
        tradeBoard.setStatus(TradeStatus.SELL);
        tradeBoard.setUser(user);
        MultipartFile firstFile = mf.get(0);
        String thumbnail = uuid;
        tradeBoard.setThumbnail(thumbnail);
        TradeBoard savedTradeBoard = tradeBoardRepository.save(tradeBoard);
        Long targetBoardId = savedTradeBoard.getId();
        try {
            MultipartFile resizedFirstFile = ResizeFile.resizeImage(firstFile, 360, 360);

            BlobInfo blobInfo = storage.create(
                    BlobInfo.newBuilder(bucketName, uuid)
                            .setContentType("image/jpg")
                            .build(),
                    resizedFirstFile.getInputStream()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for (int i = 0; i < mf.size(); i++) {
                uuid = UUID.randomUUID().toString();
                MultipartFile files = mf.get(i);
                String fileName = uuid;
                MultipartFile resizedFile = ResizeFile.resizeImage(files, 800, 600);
                BlobInfo blobInfo = storage.create(
                        BlobInfo.newBuilder(bucketName, uuid)
                                .setContentType("image/jpg")
                                .build(),
                        resizedFile.getInputStream()
                );
                TradeBoardImg tradeBoardImg = new TradeBoardImg();
                tradeBoardImg.setTradeBoard(savedTradeBoard);
                tradeBoardImg.setImgUrl(fileName);
                tradeBoardImgRepository.save(tradeBoardImg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetBoardId;
    }

    @Transactional
    public void updateBoard(Long userId, Long boardId, TradeBoardFormDto tradeBoardFormDto, List<MultipartFile> mf, List<String> imageUrls) {
        String uuid = UUID.randomUUID().toString();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        UserLocation userLocation = userLocationRepository.findByUserId(userId);

        TradeBoard tradeBoard = tradeBoardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        List<TradeBoardImg> imgList = tradeBoardImgRepository.findByTradeBoardId(boardId);
        if (user != tradeBoard.getUser()) {
            new IllegalStateException("수정할 수 있는 권한이 없습니다");
        }
        if (!mf.isEmpty()) {
            MultipartFile firstFile = mf.get(0);
            String filename = firstFile.getOriginalFilename();
            String filenameWithoutExtension = filename.substring(0, filename.lastIndexOf('.'));
            if (!filenameWithoutExtension.equals(tradeBoard.getThumbnail())) {
                // 새 파일이면
                try {
                    //기존 파일 삭제하고 저장
                    deleteFile(bucketName, tradeBoard.getThumbnail());
                    MultipartFile resizedFirstFile = ResizeFile.resizeImage(firstFile, 360, 360);
                    BlobInfo blobInfo = storage.create(
                            BlobInfo.newBuilder(bucketName, uuid)
                                    .setContentType("image/jpg")
                                    .build(),
                            resizedFirstFile.getInputStream()
                    );
                    tradeBoardRepository.save(tradeBoard);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            tradeBoardFormDto.updateEntity(tradeBoard);
            tradeBoard.setThumbnail(uuid);
            tradeBoard.setUser(user);
            tradeBoard.setModifiedDate(new Timestamp(System.currentTimeMillis()));
            tradeBoardRepository.save(tradeBoard);

            // 이미지 리스트 기존 파일 삭제, DB 삭제

            for (TradeBoardImg tradeBoardImg : imgList) {
                // Check if the image is in the list of URLs sent by the client
                if (!imageUrls.contains(tradeBoardImg.getImgUrl())) {
                    // If not, delete it
                    tradeBoardImgRepository.delete(tradeBoardImg);
                    deleteFile(bucketName, tradeBoardImg.getImgUrl());
                }
            }
            try {
                for (int i = 0; i < mf.size(); i++) {
                    uuid = UUID.randomUUID().toString();
                    MultipartFile files = mf.get(i);
                    String fileName = uuid;
                    MultipartFile resizedFile = ResizeFile.resizeImage(files, 800, 600);
                    BlobInfo blobInfo = storage.create(
                            BlobInfo.newBuilder(bucketName, uuid)
                                    .setContentType("image/jpg")
                                    .build(),
                            resizedFile.getInputStream()
                    );
                    TradeBoardImg tradeBoardImg = new TradeBoardImg();
                    tradeBoardImg.setTradeBoard(tradeBoard);
                    tradeBoardImg.setImgUrl(fileName);
                    tradeBoardImgRepository.save(tradeBoardImg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateWithoutMultiFile(Long userId, Long boardId, TradeBoardFormDto tradeBoardFormDto, List<String> imageUrls) {
        String firstFile = imageUrls.get(0);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        TradeBoard tradeBoard =  tradeBoardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        tradeBoardFormDto.updateEntity(tradeBoard);
        // 순서 바뀌었을 때
        if(firstFile!=tradeBoard.getThumbnail()){
            tradeBoard.setThumbnail(firstFile);
            // 아닐때
        }else{
            tradeBoard.setThumbnail(imageUrls.get(0));
        }
        tradeBoard.setUser(user);
        tradeBoard.setModifiedDate(new Timestamp(System.currentTimeMillis()));
        tradeBoardRepository.save(tradeBoard);

        // 이미지 파일들
        List<TradeBoardImg> imgList = tradeBoardImgRepository.findByTradeBoardId(boardId);

        for (TradeBoardImg tradeBoardImg : imgList) {
            tradeBoardImgRepository.delete(tradeBoardImg);
        }
        for(String img : imageUrls){
            TradeBoardImg tradeBoardImg = new TradeBoardImg();
            tradeBoardImg.setTradeBoard(tradeBoard);
            tradeBoardImg.setImgUrl(img);
            tradeBoardImgRepository.save(tradeBoardImg);
        }
    }

    public void deleteFile(String bucketName, String objectName) {
        storage.delete(BlobId.of(bucketName, objectName));
    }

    public void deleteBoardRequest(Long tradeBoardId, Long userId){
        TradeBoard tradeBoard = tradeBoardRepository.findById(tradeBoardId).orElseThrow(EntityNotFoundException::new);
        Optional<User> user = userRepository.findById(userId);
        if(tradeBoard.getUser().getId()!=userId||!(user.get().getRole().name().equals("ADMIN"))){
            new AccessDeniedException("삭제 할 수 있는 권한이 없습니다");
        }
        tradeBoard.setModifiedDate(new Timestamp(System.currentTimeMillis()));
        tradeBoard.setStatus(TradeStatus.DELETING);
        tradeBoardRepository.save(tradeBoard);
    }

    public List<UserLocationResponseDto> getLogginedLocationList(Long userId){
        List<UserLocation> userLocations = userLocationRepository.findAllByUserId(userId);
        UserResponseDto userResponseDto = UserResponseDto.of(userRepository.findById(userId).orElseThrow(EntityNotFoundException::new));
        List<UserLocationResponseDto> response = userLocations.stream().map(UserLocationResponseDto::of).collect(Collectors.toList());
        for(UserLocationResponseDto r : response){
            System.out.println(r.getLatitude().getClass().getSimpleName());
            r.setUserId(userResponseDto);
        }
        return response;
    }
    public List<TradeBoardDto> getTradeBoardListByUser(Long userId) {
        List<TradeBoard> tradeBoards = tradeBoardRepository.findByUserId(userId);

        List<TradeBoardDto> tradeBoardDtos = new ArrayList<>();
        for (TradeBoard tradeBoard : tradeBoards) {
            TradeBoardDto tradeBoardDto = new TradeBoardDto();
            tradeBoardDto.setTitle(tradeBoard.getTitle());
            tradeBoardDto.setPrice(tradeBoard.getPrice());
            tradeBoardDto.setThumbnail(tradeBoard.getThumbnail());
            tradeBoardDto.setCreatedDate(tradeBoard.getCreatedDate());

            tradeBoardDtos.add(tradeBoardDto);
        }

        return tradeBoardDtos;
    }


    public void like(WishLikeRequestDto requestDto, Boolean like) {

        User userId = requestDto.getUserId();
        TradeBoard tradeBoardId = requestDto.getPostId();

        TradeBoard tradeBoard = tradeBoardRepository.findById(requestDto.getPostId().getId())
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));

        if (like) {
            tradeBoard.setInteresting(tradeBoard.getInteresting() + 1);
        } else {
            tradeBoard.setInteresting(tradeBoard.getInteresting() - 1);
        }

        tradeBoardRepository.save(tradeBoard);

        WishLike existingWishLike = wishLikeRepository.findByTradeBoardAndUser(tradeBoardId, userId)
                .orElse(new WishLike());

        existingWishLike.setUser(userId);
        existingWishLike.setTradeBoard(tradeBoardId);
        existingWishLike.setWish(like);

        wishLikeRepository.save(existingWishLike);

    }

    public boolean getWish(Long boardId,Long userId) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>들어옴?");
        System.out.println(boardId+"   "+userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        TradeBoard tradeBoard =  tradeBoardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);

        System.out.println(")))))))))))))))))))))))))))))))))))");
        System.out.println(user.getId());
        System.out.println(tradeBoard.getId());

        //WishLike wishLike = wishLikeRepository.findByUserAndTradeBoard(user,tradeBoard);

        WishLike existingWishLike = wishLikeRepository.findByTradeBoardAndUser(tradeBoard, user)
                .orElse(new WishLike());

        existingWishLike.setUser(user);
        existingWishLike.setTradeBoard(tradeBoard);
        existingWishLike.setWish(existingWishLike.isWish());
        wishLikeRepository.save(existingWishLike);

        System.out.println(existingWishLike.isWish());

        boolean wish = existingWishLike.isWish();

        return wish;
    }
}