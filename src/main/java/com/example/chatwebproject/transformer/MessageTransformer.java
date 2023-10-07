package com.example.chatwebproject.transformer;

import com.example.chatwebproject.model.Message;
import com.example.chatwebproject.model.dto.MessageDto;

public class MessageTransformer {
    public static MessageDto toDto(Message message){
        MessageDto messageDto = new MessageDto();
        messageDto.setSender(message.getSender().getUsername());
        messageDto.setContent(message.getContent());
        messageDto.setRoomId(message.getRoom().getId());
        messageDto.setMessageStatus(message.getMessageStatus());
        messageDto.setType(message.getType());
        messageDto.setCreatedAt(message.getCreatedAt());

        return messageDto;
    }
}
