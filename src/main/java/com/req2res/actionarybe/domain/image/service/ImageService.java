package com.req2res.actionarybe.domain.image.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";

    public String saveImage(MultipartFile file) {
        try {
            // 오늘 날짜 기준 폴더 생성
            LocalDate now = LocalDate.now();
            String datePath = String.format("%d/%02d/%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
            String folderPath = UPLOAD_DIR + "/" + datePath;

            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 파일명 충돌 방지를 위한 UUID
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID() + extension;

            File savedFile = new File(folder, newFileName);
            file.transferTo(savedFile);

            // 저장된 상대 경로 반환
            return "/" + folderPath + "/" + newFileName;

        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }
}
