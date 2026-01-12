package com.req2res.actionarybe.domain.aisummary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    /** S3에 업로드 후 "S3 Key" 반환 */
    public String upload(MultipartFile file) {
        try {
            String original = (file.getOriginalFilename() == null) ? "file" : file.getOriginalFilename();
            String safeName = URLEncoder.encode(original, StandardCharsets.UTF_8);
            String key = "ai-summary/" + UUID.randomUUID() + "_" + safeName;

            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return key;
        } catch (Exception e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

    /** S3 key로 파일 InputStream 획득 (워커에서 사용) */
    public InputStream download(String key) {
        ResponseInputStream<GetObjectResponse> stream = s3Client.getObject(b -> b.bucket(bucket).key(key));
        return stream; // caller가 close 처리
    }
}
