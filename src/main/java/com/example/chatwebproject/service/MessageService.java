package com.example.chatwebproject.service;

import com.example.chatwebproject.model.Conversation;
import com.example.chatwebproject.model.Message;
import com.example.chatwebproject.model.User;
import com.example.chatwebproject.model.enums.MessageStatus;
import com.example.chatwebproject.model.enums.MessageType;
import com.example.chatwebproject.repository.ConversationRepository;
import com.example.chatwebproject.repository.MessageRepository;
import com.example.chatwebproject.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {
    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private ConversationRepository conversationRepository;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          ConversationRepository conversationRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
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
    public void saveMessage(Message newMessage, Long senderId, Long conversationId) {
        //validate new message
//        if (newMessage != null){
//            if (newMessage.getContent() == null ||
//                    newMessage.getContent().length() == 0) {
//                throw new RuntimeException("Invalid message content");
//            }
//        }

        //validate sender id
        if (senderId == null ||
                senderId <= 0) {
            throw new RuntimeException("Invalid sender Id");
        }

        var senderOtp = this.userRepository.findById(senderId);
        if (senderOtp.isEmpty()){
            throw new RuntimeException("Not found sender");
        }
        User sender = senderOtp.get();

        //validate conversation id
        if (conversationId == null ||
                conversationId <= 0) {
            throw new RuntimeException("Invalid conversation Id");
        }

        var conversationOtp = this.conversationRepository.findById(conversationId);
        if (conversationOtp.isEmpty()){
            throw new RuntimeException("Not found conversation");
        }
        Conversation conversation = conversationOtp.get();

        newMessage.setSender(sender);
        newMessage.setConversation(conversation);
//        newMessage.setType(MessageType.CHAT);
//        newMessage.setMessageStatus(MessageStatus.ACTIVE);

        conversation.getMessages().add(newMessage);
        sender.getMessages().add(newMessage);

        this.messageRepository.save(newMessage);
        this.userRepository.save(sender);
        this.conversationRepository.save(conversation);
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
