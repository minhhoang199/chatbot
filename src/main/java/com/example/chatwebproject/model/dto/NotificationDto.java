package com.example.chatwebproject.model.dto;

import com.example.chatwebproject.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto{
    private Long id;

    private Long userId;

    private Long messageId;

    private String content;

    private NotificationType type;
}
