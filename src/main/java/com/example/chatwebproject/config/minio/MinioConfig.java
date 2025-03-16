package com.example.chatwebproject.config.minio;

import io.minio.MinioAsyncClient;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {
    private final MinIOProperties minioProperties;

    @Bean
    public MinioClient minioClient() throws MalformedURLException {
        return MinioClient.builder()
                .endpoint(new URL(minioProperties.getUrl()))
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

    @Bean
    public MinioAsyncClient minioAsyncClient() throws MalformedURLException {
        return MinioAsyncClient.builder()
                .endpoint(new URL(minioProperties.getUrl()))
                .credentials(minioProperties.getAccessKey(),
                        minioProperties.getSecretKey())
                .build();
    }
}

