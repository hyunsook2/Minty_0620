package com.Reboot.Minty.tradeBoard.service;

import com.Reboot.Minty.config.ResizeFile;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.entity.UserLocation;
import com.Reboot.Minty.member.repository.UserLocationRepository;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.tradeBoard.constant.TradeStatus;
import com.Reboot.Minty.tradeBoard.dto.*;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import com.Reboot.Minty.tradeBoard.entity.TradeBoardImg;
//import com.Reboot.Minty.tradeBoard.repository.TradeBoardCustomRepository;
import com.Reboot.Minty.tradeBoard.repository.TradeBoardCustomRepository;
import com.Reboot.Minty.tradeBoard.repository.TradeBoardImgRepository;
import com.Reboot.Minty.tradeBoard.repository.TradeBoardRepository;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class TradeBoardService {
    private final TradeBoardRepository tradeBoardRepository;

    private final TradeBoardImgRepository tradeBoardImgRepository;

    private final UserRepository userRepository;

    private final UserLocationRepository userLocationRepository;

    private final TradeBoardCustomRepository tradeBoardCustomRepository;
    @Autowired
    private Storage storage;




    @Autowired
    public TradeBoardService(TradeBoardRepository tradeBoardRepository, TradeBoardImgRepository tradeBoardImgRepository, UserRepository userRepository, UserLocationRepository userLocationRepository, TradeBoardCustomRepository tradeBoardCustomRepository) {
        this.tradeBoardRepository = tradeBoardRepository;
        this.tradeBoardImgRepository = tradeBoardImgRepository;
        this.userRepository = userRepository;
        this.userLocationRepository = userLocationRepository;
        this.tradeBoardCustomRepository = tradeBoardCustomRepository;
    }

    public Page<TradeBoardDto> getTradeBoard(TradeBoardSearchDto tradeBoardSearchDto, Pageable pageable){
        return tradeBoardCustomRepository.getTradeBoardBy(tradeBoardSearchDto, pageable);
    }


    public TradeBoard save(TradeBoard tradeBoard) {
        return tradeBoardRepository.save(tradeBoard);
    }

    public TradeBoardDetailDto findById(Long boardId) {
        TradeBoard tradeBoard = tradeBoardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        TradeBoardDetailDto dto = TradeBoardDetailDto.of(tradeBoard);
        System.out.println("of TradeBoardDetailDto" + dto.getTopCategory());
        if(dto.getTradeStatus().equals(TradeStatus.BANNED)){
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
        UserLocation userLocation = userLocationRepository.findByUserId(user.getId());
        TradeBoard tradeBoard = tradeBoardFormDto.toEntity(tradeBoardFormDto);
        tradeBoard.setStatus(TradeStatus.SELL);
        tradeBoard.setUser(user);
        tradeBoard.setUserLocation(userLocation);
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
            tradeBoard.setUserLocation(userLocation);
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
        UserLocation userLocation = userLocationRepository.findByUserId(userId);
        tradeBoardFormDto.updateEntity(tradeBoard);
        // 순서 바뀌었을 때
        if(firstFile!=tradeBoard.getThumbnail()){
            tradeBoard.setThumbnail(firstFile);
            // 아닐때
        }else{
            tradeBoard.setThumbnail(imageUrls.get(0));
        }
        tradeBoard.setUser(user);
        tradeBoard.setUserLocation(userLocation);
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



}