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
        final String fileUploadPath = mediaOutputPath + separator + "users" + separator + senderId;   // -> /upload/users/senderId
        File targetFolder = new File(fileUploadPath);

        if (!targetFolder.exists()) {   //checking if the folder already exists with the given path,if not
            boolean folderCreated = targetFolder.mkdir();  //creating if not exist
            if (!folderCreated) {
                log.warn("Failed to create the target folder: {}", targetFolder);  //if unable to create
                return null;
            }
        }
        final String fileExtension = getFileExtension(file.getOriginalFilename());   //getting the original files extension
        final String targetFilePath = targetFolder + separator + currentTimeMillis() + "." + fileExtension;  // -> /upload/users/senderId/(currentTimeMillis+ . +fileExtension)
        Path targetpath = Paths.get(targetFilePath); //Getting the target  final path
        try {
            Files.write(targetpath, file.getBytes());  //uploading on that path
            log.info("File saved to: {}", targetFilePath);
            return targetFilePath;  //returning Files uploaded path
        } catch (IOException e) {
            log.error("File was not saved", e);
        }
        return null;
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
