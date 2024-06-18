package com.example.chatwebproject.service;

//22/06: Update add new message saveMessage() method
import com.example.chatwebproject.model.Message;
import com.example.chatwebproject.model.Room;
import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.model.enums.MessageStatus;
import com.example.chatwebproject.model.enums.MessageType;
import com.example.chatwebproject.repository.RoomRepository;
import com.example.chatwebproject.repository.MessageRepository;
import com.example.chatwebproject.repository.UserRepository;
import com.example.chatwebproject.transformer.MessageTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageService {
    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private RoomRepository roomRepository;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          RoomRepository roomRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    public List<MessageDto> getAllMessages(Long roomId){
        List<Message> messages = this.messageRepository.findAllByRoomId(roomId);
        List<MessageDto> messageDtos = new ArrayList<>();
        for (Message message: messages
             ) {
            if (message.getMessageStatus().equals(MessageStatus.ACTIVE)){
                messageDtos.add(MessageTransformer.toDto(message));
            }
        }
        return messageDtos;
    }

    //Add new message
    @Transactional
    public void saveMessage(MessageDto messageDto) {
        Long roomId = messageDto.getRoomId();
        //validate sender
        var sender = this.userRepository.findById(messageDto.getSenderId()).orElseThrow(() ->  new RuntimeException("Not found sender with id: " + messageDto.getSenderId()));

        //Check user already in the room or not ?
        Set<Room> setRoom = sender.getRooms().stream().filter(room -> room.getId() == roomId).collect(Collectors.toSet());
        Message newMsg = new Message();
        if (messageDto.getType().equals(MessageType.JOIN)){
             if (!setRoom.isEmpty()) {
                log.error("User " + sender.getUsername() + " already in the room");
                return;
            } else newMsg.setContent(messageDto.getSender() + " has joined");
        } else if (messageDto.getType().equals(MessageType.LEAVE)){
            if (setRoom.isEmpty()) {
                log.error("User " + sender.getUsername() + " not in the room");
                return;
            } else newMsg.setContent(messageDto.getSender() + " has left");
        } else newMsg.setContent(messageDto.getContent());

        //validate room id
        if (roomId == null ||
                roomId <= 0) {
            throw new RuntimeException("Invalid room Id");
        }

        var room = this.roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Not found room with Id: " + roomId));

        newMsg.setType(messageDto.getType());
        newMsg.setMessageStatus(MessageStatus.ACTIVE);

        newMsg.setSender(sender);
        newMsg.setRoom(room);

        this.messageRepository.save(newMsg);
        messageDto.setCreatedAt(LocalDateTime.now());
        messageDto.setUpdatedAt(LocalDateTime.now());

        room.setLastMessageContent(newMsg.getContent());
        room.setLastMessageTime(messageDto.getUpdatedAt());
    }

    //Edit message
    public void editMessage(Long messageId, String newContent) {
        if (messageId == null ||
                messageId <= 0) {
            throw new RuntimeException("Invalid message Id");
        }

        var messageOtp = this.messageRepository.findById(messageId);
        if (messageOtp.isEmpty()){
            throw new RuntimeException("Not found message");
        }

        Message currentMessage = messageOtp.get();

        if (newContent == null ||
                newContent.length() == 0) {
            throw new RuntimeException("Invalid message content");
        }
        currentMessage.setContent(newContent);
        messageRepository.save(currentMessage);
    }

    //Delete/Deactive message
    public void deactiveMessage(Long messageId){
        if (messageId == null ||
                messageId <= 0) {
            throw new RuntimeException("Invalid message Id");
        }

        var messageOtp = this.messageRepository.findById(messageId);
        if (messageOtp.isEmpty()){
            throw new RuntimeException("Not found message");
        }
        Message currentMessage = messageOtp.get();
        currentMessage.setMessageStatus(MessageStatus.DEACTIVE);

        this.messageRepository.save(currentMessage);
    }
}
