package com.designer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsS3Config {

    @Value("${aws.s3.region}")
    private String region;

    /**
     * S3Client Bean
     * - 파일 목록 조회
     * - 객체 삭제
     * - 메타데이터 조회 등 일반 작업용
     */
    @Bean
    public S3Client s3Client() {

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider())
                .build();
    }

    /**
     * Presigned URL 생성을 위한 Bean
     * - 업로드용 URL
     * - 다운로드용 URL
     */
    @Bean
    public S3Presigner s3Presigner() {

        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider())
                .build();
    }

    /**
     * AWS 인증 정보 설정
     *
     * 기본 권장 방식:
     * 1순위: 환경변수 (AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY)
     * 2순위: ~/.aws/credentials
     *
     * DefaultCredentialsProvider는 위 순서를 자동으로 탐색함.
     */
    @Bean
    public AwsCredentialsProvider credentialsProvider() {
        return DefaultCredentialsProvider.create();
    }
}
