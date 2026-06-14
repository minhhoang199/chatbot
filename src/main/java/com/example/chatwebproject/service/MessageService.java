package com.example.chatwebproject.service;

//22/06: Update add new message saveMessage() method
import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ChatApplicationException;
import com.example.chatwebproject.exception.ValidationRequestException;
import com.example.chatwebproject.model.dto.AttachedFileDto;
import com.example.chatwebproject.model.entity.AttachedFile;
import com.example.chatwebproject.model.entity.Message;
import com.example.chatwebproject.model.entity.Room;
import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.model.dto.NotificationDto;
import com.example.chatwebproject.model.entity.User;
import com.example.chatwebproject.model.enums.MessageStatus;
import com.example.chatwebproject.model.enums.MessageType;
import com.example.chatwebproject.model.enums.NotificationType;
import com.example.chatwebproject.model.enums.RoomStatus;
import com.example.chatwebproject.repository.AttachedFileRepository;
import com.example.chatwebproject.repository.RoomRepository;
import com.example.chatwebproject.repository.MessageRepository;
import com.example.chatwebproject.transformer.MessageTransformer;
import com.example.chatwebproject.utils.DateUtil;
import com.example.chatwebproject.utils.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;
    private final RoomRepository roomRepository;
    private final UserService userService;
    private final MessageEditHistoryService messageEditHistoryService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * Extract mentioned emails from message content (pattern: @email)
     * and create MENTION notifications for each mentioned user
     */
    private void createMentionNotifications(String content, Long messageId, Long roomId) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        // Pattern to find @email (basic email format)
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("@([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");
        java.util.regex.Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String mentionedEmail = matcher.group(1);
            try {
                NotificationDto notificationDto = new NotificationDto();
                notificationDto.setMessageId(messageId);
                notificationDto.setContent("You were mentioned in a message");
                notificationDto.setType(NotificationType.MENTION);
                // Set userId based on the mentioned email
                var userOpt = userService.getUserInfo(mentionedEmail);
                notificationDto.setUserId(userOpt.getId());
                notificationService.createNotification(notificationDto);
            } catch (Exception e) {
                log.warn("Failed to create mention notification for email: " + mentionedEmail, e);
            }
        }
    }

    /**
     * Create MESSAGE_ADD notifications for all room members except the sender
     */
    private void createMessageAddNotifications(Long messageId, Long roomId, Long senderId) {
        try {
            Room room = this.roomRepository.findById(roomId)
                    .orElseThrow(() -> new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Room not found"}));

            for (User member : room.getUsers()) {
                // Skip the sender
                if (member.getId().equals(senderId)) {
                    continue;
                }
                try {
                    NotificationDto notificationDto = new NotificationDto();
                    notificationDto.setMessageId(messageId);
                    notificationDto.setRoomId(roomId);
                    notificationDto.setUserId(member.getId());
                    notificationDto.setContent("New message in " + room.getName());
                    notificationDto.setType(NotificationType.MESSAGE_ADD);
                    this.notificationService.createNotification(notificationDto);
                } catch (Exception e) {
                    log.warn("Failed to create MESSAGE_ADD notification for user: " + member.getId(), e);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to create MESSAGE_ADD notifications for room: " + roomId, e);
        }
    }

    public List<MessageDto> getLimitMessages(Long roomId, Instant before, Integer limit){
        try {
            List<Message> messages = this.messageRepository.findAllByRoomId(roomId, before, PageRequest.of(0, limit));
            List<MessageDto> messageDtos = new ArrayList<>();
            for (Message message: messages
                 ) {
                if (message.getMessageStatus().equals(MessageStatus.ACTIVE)){
                    if (message.getSender() != null) {
                        message.setLinkAvatar(message.getSender().getLinkAvatar());
                    }
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

    public List<MessageDto> getAllMessagesFromTo(Long roomId, Instant from, Instant to){
        try {
            List<Message> messages = this.messageRepository.findAllByRoomIdFromTo(roomId, from, to);
            List<MessageDto> messageDtos = new ArrayList<>();
            for (Message message: messages
            ) {
                if (message.getMessageStatus().equals(MessageStatus.ACTIVE)){
                    if (message.getSender() != null) {
                        message.setLinkAvatar(message.getSender().getLinkAvatar());
                    }
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
                    if (message.getSender() != null) {
                        message.setLinkAvatar(message.getSender().getLinkAvatar());
                    }
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
            var room = this.roomRepository.findById(roomId).orElseThrow(
                    () -> new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"Not found room with Id: " + roomId}, null));
            if (room.getStatus().equals(RoomStatus.BLOCKED) || room.getStatus().equals(RoomStatus.DISABLE)) {
                throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"User can not send message in this room: " + roomId}, null);
            }

            newMsg.setType(messageDto.getType());
            newMsg.setMessageStatus(MessageStatus.ACTIVE);
            newMsg.setSender(sender);
            newMsg.setRoom(room);
            newMsg.setRemovedEmails(messageDto.getRemovedEmails());
            newMsg.setLinkAvatar(sender.getLinkAvatar());

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
            newMsg.setCreatedAt(Instant.now());
            newMsg.setUpdatedAt(Instant.now());
            newMsg.setDelFlag(false);
            newMsg = this.messageRepository.save(newMsg);

            // Create MENTION and MESSAGE_ADD notifications
            this.createMentionNotifications(messageDto.getContent(), newMsg.getId(), roomId);
            this.createMessageAddNotifications(newMsg.getId(), roomId, sender.getId());


            room.setLastMessageContent(newMsg.getContent());
            room.setLastMessageTime(newMsg.getUpdatedAt());

            MessageDto toDto = MessageTransformer.toDto(newMsg);
            String destination = "/topic/rooms/" + roomId;
            messagingTemplate.convertAndSend(destination, toDto);
            return toDto;
        } catch (JsonProcessingException e) {
            throw new ChatApplicationException(DomainCode.INTERNAL_SERVICE_ERROR, new Object[]{"Save message failed " + e});
        }
    }

    //Edit message
    @Transactional
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
            if (!StringUtils.equals(currentMessage.getContent(), updateMessage.getContent())) {
                Instant now = DateUtil.localDateTimeToInstant(DateUtil.getCurrentDate());
                Instant limitTime = currentMessage.getCreatedAt().plus(10, ChronoUnit.MINUTES);
                if (now.isAfter(limitTime)) {
                    throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Message can only be edited within 10 minutes of being sent."});
                }
                this.messageEditHistoryService.save(currentMessage.getContent(), updateMessage.getId());
                currentMessage.setEdited(true);
            }
            currentMessage.setContent(updateMessage.getContent());
            currentMessage.setEmoji(objectMapper.writeValueAsString(updateMessage.getEmoji()));
            messageRepository.save(currentMessage);

            // Create MENTION notifications if edited message contains @mentions
            this.createMentionNotifications(updateMessage.getContent(), currentMessage.getId(), currentMessage.getRoom().getId());

            return MessageTransformer.toDto(currentMessage);
        } catch (JsonProcessingException e) {
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
