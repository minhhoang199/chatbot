package com.example.chatwebproject.service;

//22/06: Update add new message saveMessage() method
import com.example.chatwebproject.model.Room;
import com.example.chatwebproject.model.Message;
import com.example.chatwebproject.model.User;
import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.model.enums.MessageStatus;
import com.example.chatwebproject.repository.RoomRepository;
import com.example.chatwebproject.repository.MessageRepository;
import com.example.chatwebproject.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
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

    public List<Message> getAllMessage(String content){
//        if (content == null ||
//                content.length() == 0) {
//            throw new RuntimeException("Invalid message content");
//        }
        return messageRepository.findByContentContaining(content);
    }

    //Add new message
    @Transactional
    public void saveMessage(MessageDto messageDto, Long roomId) {
        Message newMsg = new Message();
        newMsg.setContent(messageDto.getContent());
        newMsg.setType(messageDto.getMessageType());
        newMsg.setMessageStatus(messageDto.getMessageStatus());

        //validate sender phone
        String senderPhone = messageDto.getSenderPhone();
        var senderOtp = this.userRepository.findByPhone(senderPhone);
        if (senderOtp.isEmpty()){
            throw new RuntimeException("Not found sender");
        }
        User sender = senderOtp.get();

        //validate room id
        if (roomId == null ||
                roomId <= 0) {
            throw new RuntimeException("Invalid room Id");
        }

        var roomOtp = this.roomRepository.findById(roomId);
        if (roomOtp.isEmpty()){
            throw new RuntimeException("Not found room");
        }
        Room room = roomOtp.get();

//        newMsg.setSender(sender);
        newMsg.setRoom(room);

        room.getMessages().add(newMsg);
//        sender.getMessages().add(newMsg);

        this.messageRepository.save(newMsg);
        this.userRepository.save(sender);
        this.roomRepository.save(room);
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
