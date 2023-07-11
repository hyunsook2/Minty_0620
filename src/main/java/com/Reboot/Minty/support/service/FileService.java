package com.Reboot.Minty.support.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {
    private final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "files" + File.separator;

    public String getUploadDir() {
        return UPLOAD_DIR;
    }

    public List<String> saveFiles(List<MultipartFile> files) throws IOException {
        List<String> filePaths = new ArrayList<>();

        for (MultipartFile file: files) {
            if (file.isEmpty()) {
                continue;
            }
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            String filePath = UPLOAD_DIR + fileName;

            Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            filePaths.add(filePath);
        }

        return filePaths;
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = FilenameUtils.getExtension(originalFilename);
        String baseName = FilenameUtils.getBaseName(originalFilename);
        return baseName +"."+ extension;
    }

    public Resource getFile(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path is null");
        }
        String sanitizedFilePath = filePath.replace("\\", "/");
        File file = new File(sanitizedFilePath);

        if (file.exists()) {
            Resource resource = new UrlResource(file.toURI());

            if (resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("File is not readable: " + filePath);
            }
        } else {
            throw new FileNotFoundException("File not found: " + filePath);
        }
    }

}

