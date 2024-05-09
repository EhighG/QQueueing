package com.qqueueing.main.registration.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {
    public String uploadImage(MultipartFile file) {
        try {
            // 파일 저장
            String fileUrl = saveFile(file);
            return fileUrl;
        } catch (Exception e) {
            // 파일 저장 실패 시 예외 처리
            throw new RuntimeException("Error saving image file", e);
            // 예외 처리 로직 추가
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        // 파일 저장 로직
        String fileName = UUID.randomUUID().toString() + "." + getFileExtension(file.getOriginalFilename());
        Path filePath = Paths.get("/home/qqueueing/uploads", fileName);
        Files.write(filePath, file.getBytes());
        return "/uploads/" + fileName;
    }

    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // 확장자가 없는 경우
        }
        return fileName.substring(lastIndexOf + 1);
    }
}
