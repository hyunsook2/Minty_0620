package com.Reboot.Minty.support.controller;

import com.Reboot.Minty.support.dto.FileDto;
import com.Reboot.Minty.support.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class FileDtoConverter implements Converter<MultipartFile, FileDto> {

    @Autowired
    FileService fileService;

    @Override
    public FileDto convert(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String fileName = getOriginalFilenameFromPath(multipartFile.getOriginalFilename());
        String filePath = fileService.getUploadDir() + fileName;

        try {
            Files.copy(multipartFile.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FileDto.builder()
                .filename(fileName)
                .origFilename(multipartFile.getOriginalFilename())
                .filePath(filePath)
                .build();
    }

    private String getOriginalFilenameFromPath(String filePath) {
        Path path = Paths.get(filePath);
        return path.getFileName().toString();
    }
}
