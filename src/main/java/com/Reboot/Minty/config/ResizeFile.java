package com.Reboot.Minty.config;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ResizeFile {

    public static MultipartFile resizeImage(MultipartFile file, int width, int height) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        if (originalWidth <= width && originalHeight <= height) {
            return file;
        }
        Thumbnails.Builder<? extends InputStream> thumbnailBuilder = Thumbnails.of(file.getInputStream())
                .size(width, height);

        if (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/jpg") || file.getContentType().equals("image/png") || file.getContentType().equals("image/bmp")) {
            thumbnailBuilder.outputFormat("jpg");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        thumbnailBuilder.toOutputStream(outputStream);

        return new MultipartFile() {
            @Override
            public String getName() {
                return file.getName();
            }

            @Override
            public String getOriginalFilename() {
                return file.getOriginalFilename();
            }

            @Override
            public String getContentType() {
                return file.getContentType();
            }

            @Override
            public boolean isEmpty() {
                return file.isEmpty();
            }

            @Override
            public long getSize() {
                return outputStream.size();
            }

            @Override
            public byte[] getBytes() throws IOException {
                return outputStream.toByteArray();
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(outputStream.toByteArray());
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                file.transferTo(dest);
            }
        };
    }
}
