package com.req2res.actionarybe.domain.image.controller;

import com.req2res.actionarybe.domain.image.dto.ImageUploadResponseDTO;
import com.req2res.actionarybe.domain.image.service.ImageService;
import com.req2res.actionarybe.global.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {
    private final ImageService imageService;

    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<ImageUploadResponseDTO>> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = imageService.saveImage(file);
        ImageUploadResponseDTO response = new ImageUploadResponseDTO(imageUrl);
        return ResponseEntity.ok(Response.success("이미지 업로드 성공", response));
    }
}
