package com.Reboot.Minty.chat.controller;

import com.Reboot.Minty.chat.Entity.ImageInfo;
import com.Reboot.Minty.chat.repository.ImageRepository;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Controller
public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private Storage storage;

    @Value("${spring.cloud.gcp.storage.credentials.bucket}")
    private String bucketName;

    @PostMapping("/sendImage")
    public ResponseEntity<?> handleFileUpload(@RequestParam("image") MultipartFile file) {
        try {
            if (!file.getContentType().startsWith("image/")) {
                return new ResponseEntity<>("Invalid image file", HttpStatus.BAD_REQUEST);
            }

            String uuid = UUID.randomUUID().toString();
            String fileExtension = "images"; //
            String imagePath = uuid +fileExtension;
            System.out.println("Uploading the image to: " + imagePath);
            ImageInfo imageInfo = new ImageInfo(imagePath);

            // 이미지 정보 DB 저장
            ImageInfo savedImageInfo = imageRepository.save(imageInfo);
            // 이미지 정보 DB 저장 후
            System.out.println("Image info saved successfully.");

            BlobInfo blobInfo = storage.create(
                    BlobInfo.newBuilder(bucketName, imagePath)
                            .setContentType("image/jpg")
                            .build(),
                    file.getInputStream()
            );
            return new ResponseEntity<>(savedImageInfo.getFilePath(), HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload the image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}