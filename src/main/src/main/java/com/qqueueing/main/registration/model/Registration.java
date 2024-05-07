package com.qqueueing.main.registration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Document(collection = "registration_info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

    @Id
    private String id; // 자동 생성되는 식별자
    private String topicName;
    private String targetUrl;
    private Integer maxCapacity; // 최대 수용 인원
    private Integer processingPerMinute; // 1분당 처리 인원
    private String serviceName; // 서비스명
    private String queueImageUrl; // 대기열 이미지
    @Setter
    private Boolean isActive = false; // 활성화 여부
    @Setter
    private Integer partitionNo;

    public Registration(String topicName, String targetUrl) {
        this.topicName = topicName;
        this.targetUrl = targetUrl;
        this.isActive = false;
    }

    public void uploadImage(MultipartFile file) {
        try {
            // 파일 저장
            String fileUrl = saveFile(file);
            this.queueImageUrl = fileUrl;
        } catch (Exception e) {
            // 파일 저장 실패 시 예외 처리
            // 예외 처리 로직 추가
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        // 파일 저장 로직
        String fileName = UUID.randomUUID().toString() + "." + getFileExtension(file.getOriginalFilename());
        Path filePath = Paths.get("src/main/resources/static/uploads", fileName);
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
