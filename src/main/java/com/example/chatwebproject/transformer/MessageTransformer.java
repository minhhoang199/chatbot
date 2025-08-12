package com.example.chatwebproject.transformer;

import com.example.chatwebproject.model.entity.Message;
import com.example.chatwebproject.model.dto.MessageDto;
import org.springframework.util.CollectionUtils;

import java.util.stream.Collectors;

public class MessageTransformer {
    public static MessageDto toDto(Message message){
        MessageDto messageDto = new MessageDto();
        messageDto.setId(message.getId());
        messageDto.setSender(message.getSender() != null ? message.getSender().getUsername() : null);
        messageDto.setSenderId(message.getSender() != null ? message.getSender().getId() : null);
        messageDto.setContent(message.getContent());
        messageDto.setRoomId(message.getRoom().getId());
        messageDto.setMessageStatus(message.getMessageStatus());
        messageDto.setType(message.getType());
        messageDto.setEmoji(message.getEmoji());
        messageDto.setCreatedAt(message.getCreatedAt());
        messageDto.setUpdatedAt(message.getUpdatedAt());
        if (message.getReplyMessage() != null) {
            messageDto.setIsReply(true);
            messageDto.setReplyId(message.getReplyMessage().getId());
            messageDto.setReplyContent(message.getReplyMessage().getContent());
        }

        if (CollectionUtils.isEmpty(message.getAttachedFiles())){
            messageDto.setAttachedFiles(message.getAttachedFiles().stream().map(AttachedFileTransformer::toDto).collect(Collectors.toSet()));
        }
        return messageDto;
    }
}
