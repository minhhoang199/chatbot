package com.example.chatwebproject;

import com.example.chatwebproject.config.minio.MinIOProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class ChatWebProjectApplication{

    public static void main(String[] args) {
        SpringApplication.run(ChatWebProjectApplication.class, args);
    }
}
