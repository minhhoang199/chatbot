package com.example.chatwebproject.model.dto;

import com.example.chatwebproject.model.entity.BaseEntity;
import com.example.chatwebproject.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto extends BaseEntity {
    private Long id;

    private Long userId;

    private Long messageId;

    private String content;

    private NotificationType type;

    private Boolean isRead;
}
