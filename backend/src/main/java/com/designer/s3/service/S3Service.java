package com.designer.s3.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    /**
     * 업로드용 Presigned URL 생성
     */
    String generateUploadPresignedUrl(String key, String contentType);

    /**
     * 다운로드용 Presigned URL 생성
     */
    String generateDownloadPresignedUrl(String key);

    /**
     * 서버에서 직접 파일 업로드
     */
    void uploadFile(String key, MultipartFile file);

    /**
     * 객체 삭제
     */
    void deleteObject(String key);
}
