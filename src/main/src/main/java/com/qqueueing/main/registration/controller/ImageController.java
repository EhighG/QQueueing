package com.qqueueing.main.registration.controller;


import com.qqueueing.main.common.SuccessResponse;
import com.qqueueing.main.registration.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/image")
@RestController
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        String fileUrl = imageService.uploadImage(file);
        String message = "이미지 등록에 성공했습니다.";
        SuccessResponse response = new SuccessResponse(HttpStatus.OK.value(), message, fileUrl);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
