package com.qqueueing.main.registration.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
        Path filePath = Paths.get("/var/lib/uploads", fileName);
        Files.createDirectories(filePath.getParent());

        try {
            // 파일 쓰기
            Files.write(filePath, file.getBytes());
            System.out.println("파일 쓰기 완료");
        } catch (IOException e) {
            // 파일 쓰기 실패
            e.printStackTrace();
            throw e;
        }

        // 파일 경로 확인
        File savedFile = filePath.toFile();
        if (savedFile.isFile()) {
            System.out.println("파일이 정상적으로 저장되었습니다: " + savedFile.getAbsolutePath());
        } else {
            System.out.println("파일 저장에 실패했습니다.");
        }
        return savedFile.getAbsolutePath();
    }

    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // 확장자가 없는 경우
        }
        return fileName.substring(lastIndexOf + 1);
    }
}
