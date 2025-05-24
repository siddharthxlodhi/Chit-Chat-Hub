package com.sid.chitchathub.file;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;
import static java.lang.System.currentTimeMillis;

@Slf4j
@Service
public class FileStorageService {

    // -> /upload
    @Value("${application.file.uploads.media-output-path}")
    private String mediaOutputPath;

    public String saveFile(@NonNull String senderId, @NonNull MultipartFile file) {
        // Build the folder path using Paths.get to join safely
        Path targetFolderPath = Paths.get(mediaOutputPath, "users", senderId);

        // Create directories if not exist
        try {
            Files.createDirectories(targetFolderPath);
        } catch (IOException e) {
            log.warn("Failed to create target folder: {}", targetFolderPath, e);
            return null;
        }

        // Get file extension safely (handle null original filename)
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename != null ? originalFilename : "");

        // Build the full target file path
        String filename = System.currentTimeMillis() + "." + fileExtension;
        Path targetFilePath = targetFolderPath.resolve(filename);

        try {
            // Write file bytes to the target path
            Files.write(targetFilePath, file.getBytes());
            log.info("File saved to: {}", targetFilePath.toAbsolutePath());
            return targetFilePath.toString();
        } catch (IOException e) {
            log.error("File was not saved", e);
            return null;
        }
    }


    //it will return the extension of the file
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }
}
