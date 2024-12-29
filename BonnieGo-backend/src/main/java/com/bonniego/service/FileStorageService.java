package com.bonniego.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}") // 從 application.properties 取得上傳目錄
    private String uploadDir;

    public String storeFile(MultipartFile file) throws IOException {
        // 生成唯一檔名，避免重覆檔案
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // 儲存圖片到指定目錄
        String filePath = Paths.get(uploadDir, fileName).toString();
        File destination = new File(filePath);
        file.transferTo(destination);

        // 返回公開圖片 URL
        return "/uploads/" + fileName;
    }
}
