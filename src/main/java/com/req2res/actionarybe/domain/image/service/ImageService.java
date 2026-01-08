package com.req2res.actionarybe.domain.image.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

	private final S3Template s3Template;

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucket;

	// private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";

	public String saveImage(MultipartFile file) {

		// 오늘 날짜 기준 폴더 생성
		LocalDate now = LocalDate.now();
		String datePath = String.format("%d/%02d/%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
		// String folderPath = UPLOAD_DIR + "/" + datePath;

		// File folder = new File(folderPath);
		// if (!folder.exists()) {
		// 	folder.mkdirs();
		// }

		// 파일명 충돌 방지를 위한 UUID
		String originalFilename = file.getOriginalFilename();
		String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		String newFileName = UUID.randomUUID() + extension;

		// File savedFile = new File(folder, newFileName);
		// file.transferTo(savedFile);
		//
		// // 저장된 상대 경로 반환
		// return "/" + folderPath + "/" + newFileName;

		String s3Key = "uploads/" + datePath + "/" + newFileName;

		try (InputStream inputStream = file.getInputStream()) {
			S3Resource resource = s3Template.upload(bucket, s3Key, inputStream,
				ObjectMetadata.builder()
					.contentType(file.getContentType())
					.build());

			return resource.getURL().toString();

		} catch (IOException e) {
			throw new RuntimeException("이미지 저장 실패", e);
		}
	}

	public void deleteImage(String fileUrl) {
		String key = fileUrl.substring(fileUrl.lastIndexOf(".com/") + 5);
		s3Template.deleteObject(bucket, key);
	}
}
