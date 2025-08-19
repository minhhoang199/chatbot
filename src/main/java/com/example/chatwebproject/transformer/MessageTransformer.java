package com.example.chatwebproject.transformer;

import com.example.chatwebproject.model.dto.AttachedFileDto;
import com.example.chatwebproject.model.entity.Message;
import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.utils.CommonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.util.CollectionUtils;

import java.util.stream.Collectors;

public class MessageTransformer {
    public static MessageDto toDto(Message message) throws JsonProcessingException {
        MessageDto messageDto = new MessageDto();
        messageDto.setId(message.getId());
        messageDto.setSender(message.getSender() != null ? message.getSender().getUsername() : null);
        messageDto.setSenderId(message.getSender() != null ? message.getSender().getId() : null);
        messageDto.setContent(message.getContent());
        messageDto.setRoomId(message.getRoom().getId());
        messageDto.setMessageStatus(message.getMessageStatus());
        messageDto.setType(message.getType());
        messageDto.setEmoji(CommonUtils.convertStringToEmojiObject(message.getEmoji()));
        messageDto.setCreatedAt(message.getCreatedAt());
        messageDto.setUpdatedAt(message.getUpdatedAt());
        if (message.getReplyMessage() != null) {
            messageDto.setIsReply(true);
            messageDto.setReplyId(message.getReplyMessage().getId());
            messageDto.setReplyContent(message.getReplyMessage().getContent());
        }

        if (message.getAttachedFile() != null) {
            messageDto.setAttachedFile(AttachedFileDto.builder()
                    .id(message.getAttachedFile().getId())
                    .extension(message.getAttachedFile().getExtension())
                    .linkFile(message.getAttachedFile().getLinkFile())
                    .fileName(message.getAttachedFile().getFileName())
                    .build());
        }
        return messageDto;
    }
}
