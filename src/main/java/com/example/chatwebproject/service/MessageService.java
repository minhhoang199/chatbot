package com.example.chatwebproject.service;

//22/06: Update add new message saveMessage() method
import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ChatApplicationException;
import com.example.chatwebproject.model.dto.AttachedFileDto;
import com.example.chatwebproject.model.entity.AttachedFile;
import com.example.chatwebproject.model.entity.Message;
import com.example.chatwebproject.model.entity.Room;
import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.model.enums.MessageStatus;
import com.example.chatwebproject.model.enums.MessageType;
import com.example.chatwebproject.repository.AttachedFileRepository;
import com.example.chatwebproject.repository.RoomRepository;
import com.example.chatwebproject.repository.MessageRepository;
import com.example.chatwebproject.transformer.MessageTransformer;
import com.example.chatwebproject.utils.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;
    private final RoomRepository roomRepository;
    private final UserService userService;
    @PersistenceContext
    private final EntityManager entityManager;

    public List<MessageDto> getLimitMessages(Long roomId, LocalDateTime before, Integer limit){
        try {
            List<Message> messages = this.messageRepository.findAllByRoomId(roomId, before, PageRequest.of(0, limit));
            List<MessageDto> messageDtos = new ArrayList<>();
            for (Message message: messages
                 ) {
                if (message.getMessageStatus().equals(MessageStatus.ACTIVE)){
                    messageDtos.add(MessageTransformer.toDto(message));
                }
            }
            return messageDtos.stream().sorted(new Comparator<MessageDto>() {
                @Override
                public int compare(MessageDto o1, MessageDto o2) {
                    return o1.getCreatedAt().compareTo(o2.getCreatedAt());
                }
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ChatApplicationException(DomainCode.INTERNAL_SERVICE_ERROR, new Object[]{"Get messages failed: " + e});
        }
    }

    public List<MessageDto> getAllMessagesFromTo(Long roomId, LocalDateTime from, LocalDateTime to){
        try {
            List<Message> messages = this.messageRepository.findAllByRoomIdFromTo(roomId, from, to);
            List<MessageDto> messageDtos = new ArrayList<>();
            for (Message message: messages
            ) {
                if (message.getMessageStatus().equals(MessageStatus.ACTIVE)){
                    messageDtos.add(MessageTransformer.toDto(message));
                }
            }
            return messageDtos.stream().sorted(new Comparator<MessageDto>() {
                @Override
                public int compare(MessageDto o1, MessageDto o2) {
                    return o1.getCreatedAt().compareTo(o2.getCreatedAt());
                }
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ChatApplicationException(DomainCode.INTERNAL_SERVICE_ERROR, new Object[]{"Get messages failed: " + e});
        }
    }

    public List<MessageDto> searchByContent(Long roomId, String content){
        try {
            List<Message> messages = this.messageRepository.searchByContent(roomId, content);
            List<MessageDto> messageDtos = new ArrayList<>();
            for (Message message: messages
            ) {
                if (message.getMessageStatus().equals(MessageStatus.ACTIVE)){
                    messageDtos.add(MessageTransformer.toDto(message));
                }
            }
            return messageDtos.stream().sorted(new Comparator<MessageDto>() {
                @Override
                public int compare(MessageDto o1, MessageDto o2) {
                    return o1.getCreatedAt().compareTo(o2.getCreatedAt());
                }
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ChatApplicationException(DomainCode.INTERNAL_SERVICE_ERROR, new Object[]{"Get messages failed: " + e});
        }
    }

    //Add new message
    @Transactional
    public MessageDto saveMessage(MessageDto messageDto) {
        try {
            Long roomId = messageDto.getRoomId();
            //validate sender
            var sender = this.userService.getUserInfo(messageDto.getSender());

            //Check user already in the room or not ?
            Set<Room> setRoom = sender.getRooms().stream().filter(room -> room.getId() == roomId).collect(Collectors.toSet());
            Message newMsg = new Message();
            if (messageDto.getType().equals(MessageType.JOIN)){
                 if (!setRoom.isEmpty()) {
                    log.error("User " + sender.getUsername() + " already in the room");
                    return null;
                } else newMsg.setContent(messageDto.getSender() + " has joined");
            } else if (messageDto.getType().equals(MessageType.LEAVE)){
                if (setRoom.isEmpty()) {
                    log.error("User " + sender.getUsername() + " not in the room");
                    return null;
                } else newMsg.setContent(messageDto.getSender() + " has left");
            } else newMsg.setContent(messageDto.getContent());

            //validate room id
            if (roomId == null ||
                    roomId <= 0) {
                throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Invalid room Id"});
            }

            var room = this.roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Not found room with Id: " + roomId));

            newMsg.setType(messageDto.getType());
            newMsg.setMessageStatus(MessageStatus.ACTIVE);

            newMsg.setSender(sender);
            newMsg.setRoom(room);

            //reply message
            if (messageDto.getReplyId() != null) {
                Message replyMessage = this.messageRepository.getByIdAndNotDel(messageDto.getReplyId()).orElseThrow(
                        () -> new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Not found replying message by id " + messageDto.getReplyId() })
                );
                newMsg.setReplyMessage(replyMessage);
            }

            //attached file
            if (messageDto.getAttachedFile() != null) {
                AttachedFile file = entityManager.find(AttachedFile.class, messageDto.getAttachedFile().getId());
                if (ObjectUtils.isEmpty(file)) {
                    throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Not found any file"});
                }
                newMsg.setAttachedFile(file);
            }
            newMsg.setCreatedAt(LocalDateTime.now());
            newMsg.setUpdatedAt(LocalDateTime.now());
            newMsg = this.messageRepository.save(newMsg);


            room.setLastMessageContent(newMsg.getContent());
            room.setLastMessageTime(newMsg.getUpdatedAt());

            return MessageTransformer.toDto(newMsg);
        } catch (JsonProcessingException e) {
            throw new ChatApplicationException(DomainCode.INTERNAL_SERVICE_ERROR, new Object[]{"Save message failed " + e});
        }
    }

    //Edit message
    public MessageDto editMessage(MessageDto updateMessage) {
        try {
            if (updateMessage.getId() == null) {
                throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Id must not be null"});
            }
            var messageOtp = this.messageRepository.findById(updateMessage.getId());
            if (messageOtp.isEmpty()){
                throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Not found message by id " + updateMessage.getId()});
            }

            Message currentMessage = messageOtp.get();
            currentMessage.setContent(updateMessage.getContent());
            currentMessage.setEmoji(objectMapper.writeValueAsString(updateMessage.getEmoji()));
            messageRepository.save(currentMessage);

            return MessageTransformer.toDto(currentMessage);
        } catch (Exception e) {
            throw new ChatApplicationException(DomainCode.INTERNAL_SERVICE_ERROR, new Object[]{"Edit message failed " + e});
        }
    }

    //Delete/Deactive message
    public MessageDto deactiveMessage(Long messageId){
        try {
            if (messageId == null) {
                throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Invalid message Id"});
            }

            Message currentMessage = this.messageRepository.findByIdAndSender(messageId, SecurityUtil.getCurrentUserIdLogin()).orElseThrow(
                    () -> new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Not found message by Id and sender"})
            );
            currentMessage.setMessageStatus(MessageStatus.INACTIVE);
            currentMessage.setDelFlag(true);

            this.messageRepository.save(currentMessage);
            return MessageTransformer.toDto(currentMessage);
        } catch (JsonProcessingException e) {
            throw new ChatApplicationException(DomainCode.INTERNAL_SERVICE_ERROR, new Object[]{"Delete message failed"});
        }
    }
}
