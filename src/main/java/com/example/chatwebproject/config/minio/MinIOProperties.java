package com.example.chatwebproject.config.minio;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "minio")
@Getter
@Setter
public class MinIOProperties {
    private String url;
    private String bucket;
    private String accessKey;
    private String secretKey;
    private int expiry;
    private String filePathBase;
}

