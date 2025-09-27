package com.pavel.store.service;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.*;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${app.image.bucket:C:/Users/Pavel/Desktop/store/images")
    private String bucket;


    @SneakyThrows
    public void upload(String imagePath, InputStream content) {

        Path fullImagePath = Path.of(bucket, imagePath);
        try (content) {

            Files.createDirectories(fullImagePath.getParent());
            Files.write(fullImagePath, content.readAllBytes(), CREATE, TRUNCATE_EXISTING);
        }
    }

    @SneakyThrows
    public Optional<byte[]> get(String imagePath) {
        Path fullImagePath = Path.of(bucket, imagePath);

        return Files.exists(fullImagePath) ? Optional.of(Files.readAllBytes(fullImagePath))
                : Optional.empty();
    }
}

