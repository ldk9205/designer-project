package com.designer.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.presign.upload-expiration-seconds}")
    private long uploadExpireSeconds;

    @Value("${aws.s3.presign.download-expiration-seconds}")
    private long downloadExpireSeconds;

    /**
     * ================================
     * 🔹 S3 Key 생성 (폴더 구조 적용)
     * ================================
     */
    public String generateImageKey(Long designerId,
                                   Long treatmentId,
                                   String originalFileName) {

        String uuid = UUID.randomUUID().toString();

        return "designer/" + designerId +
                "/treatment/" + treatmentId +
                "/" + uuid + "_" + originalFileName;
    }

    /**
     * 업로드용 Presigned URL 생성
     */
    @Override
    public String generateUploadPresignedUrl(String key, String contentType) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofSeconds(uploadExpireSeconds))
                        .putObjectRequest(putObjectRequest)
                        .build();

        PresignedPutObjectRequest presignedRequest =
                s3Presigner.presignPutObject(presignRequest);

        return presignedRequest.url().toString();
    }

    /**
     * 다운로드용 Presigned URL 생성
     */
    @Override
    public String generateDownloadPresignedUrl(String key) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofSeconds(downloadExpireSeconds))
                        .getObjectRequest(getObjectRequest)
                        .build();

        PresignedGetObjectRequest presignedRequest =
                s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }

    /**
     * 서버 직접 업로드
     * (폴더 구조 적용하려면 generateImageKey로 key 생성 후 전달)
     */
    @Override
    public void uploadFile(String key, MultipartFile file) {

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 중 오류 발생", e);
        }
    }

    /**
     * 객체 삭제
     */
    @Override
    public void deleteObject(String key) {

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public void deleteObjectsByPrefix(String prefix) {
        String continuationToken = null;

        do {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .continuationToken(continuationToken)
                    .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

            if (listResponse.hasContents() && !listResponse.contents().isEmpty()) {
                java.util.List<ObjectIdentifier> objectsToDelete = listResponse.contents().stream()
                        .map(s3Object -> ObjectIdentifier.builder()
                                .key(s3Object.key())
                                .build())
                        .toList();

                DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                        .bucket(bucketName)
                        .delete(Delete.builder()
                                .objects(objectsToDelete)
                                .build())
                        .build();

                s3Client.deleteObjects(deleteRequest);
            }

            continuationToken = listResponse.nextContinuationToken();
        } while (continuationToken != null);
    }
}
