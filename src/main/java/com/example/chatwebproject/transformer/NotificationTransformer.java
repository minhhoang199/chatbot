package com.example.chatwebproject.transformer;

import com.example.chatwebproject.model.dto.NotificationDto;
import com.example.chatwebproject.model.entity.Notification;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationTransformer {
    public static NotificationDto toDto(Notification entity){
        if (ObjectUtils.isEmpty(entity)) return null;
        NotificationDto dto = new NotificationDto();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setType(entity.getType());
        dto.setMessageId(entity.getMessageId());
        dto.setRoomId(entity.getRoomId());
        dto.setUserId(entity.getUserId());
        dto.setIsRead(entity.getIsRead());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public static List<NotificationDto> toDtoList(List<Notification> users) {
        return users.stream().map(NotificationTransformer::toDto).collect(Collectors.toList());
    }

}
