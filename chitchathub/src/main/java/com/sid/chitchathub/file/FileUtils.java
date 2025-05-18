package com.sid.chitchathub.file;

import ch.qos.logback.core.util.StringUtil;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//It is a utility class(private constructor)
@Slf4j
public class FileUtils {


    private FileUtils() {
    }

    public static byte[] readFileFromLocation(String filePath) {

        if (StringUtils.isBlank(filePath)) {
            return new byte[0];
        }
        try {
            Path path = Paths.get(filePath);  //reading file path
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.warn("No file found at:{}", filePath);
        }
        return new byte[0];
    }

}
