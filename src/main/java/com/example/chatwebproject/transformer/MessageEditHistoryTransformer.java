package com.example.chatwebproject.transformer;

import com.example.chatwebproject.model.dto.MessageEditHistoryDto;
import com.example.chatwebproject.model.entity.MessageEditHistory;

public class MessageEditHistoryTransformer {
    public static MessageEditHistoryDto toDto(MessageEditHistory entity) {
        MessageEditHistoryDto dto = new MessageEditHistoryDto();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setDelFlag(entity.getDelFlag());
        return dto;
    }
}
