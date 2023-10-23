package com.example.sample1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AppConfiguration {
    // application,properties 파일의 값을 읽어서 변수에 주입
    @Value("${aws.accessKeyId}")
    private String accessKeyId;
    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;

    @Bean
    public S3Client s3Client() {    // AWS S3 클라이언트 객체 생성 및 초기화 후 빈 등록
        // AWS의 기본 인증 정보를 생성
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        // 생성한 인증 정보를 사용하여 정적 크리덴셜 프로바이더를 생성
        // 크리덴셜 프로바이더: AWS SDK에서 인증정보를 관리하고 제공하는 역할을 하는 컴포넌트
        // 즉, (accessKeyId, secretAccessKey)를 안전하게 저장하고 필용할 떄 해당 정보를 제공하며,
        AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);

        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(provider)
                .build();   // S3 클라이언트 객체를 생성
    }
}
