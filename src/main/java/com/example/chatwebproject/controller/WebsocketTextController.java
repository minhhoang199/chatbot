package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.service.MessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketTextController {
    private MessageService messageService;
    private SimpMessageSendingOperations messagingTemplate;

    public WebsocketTextController(MessageService messageService, SimpMessageSendingOperations messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

//    @MessageMapping("/sendMessage/{conversationId}/users/{senderId}")
//    public Message sendMessage(@Payload Message message,
//                               @DestinationVariable Long conversationId,
//                               @DestinationVariable Long senderId){
//        messagingTemplate.convertAndSend("/topic/public/" + conversationId, message);
//        this.messageService.saveMessage(message, senderId, conversationId);
//        return message;
//    }

//    @MessageMapping("/chat.register/{conversationId}/users/{senderId}")
//    public Message register(@Payload Message message,
//                            @DestinationVariable Long conversationId,
//                            @DestinationVariable Long senderId,
//                            SimpMessageHeaderAccessor headerAccessor) {
//        messagingTemplate.convertAndSend("/topic/public/" + conversationId, message);
//        this.messageService.saveMessage(message, senderId, conversationId);
//        headerAccessor.getSessionAttributes().put("username", message.getSender().getUsername());
//        return message;
//    }

    @MessageMapping("/sendMessage/{conversationId}/users/{senderId}")
    public MessageDto sendMessage(@Payload MessageDto messageDto,
                                  @DestinationVariable Long conversationId,
                                  @DestinationVariable Long senderId,
                                  SimpMessageHeaderAccessor headerAccessor){
        messagingTemplate.convertAndSend("/topic/public/" + conversationId, messageDto);
        this.messageService.saveMessage(messageDto, conversationId);
        return messageDto;
    }

    @MessageMapping("/chat.register/{conversationId}/users/{senderId}")
    public MessageDto register(@Payload MessageDto messageDto,
                               @DestinationVariable Long conversationId,
                               @DestinationVariable Long senderId,
                               SimpMessageHeaderAccessor headerAccessor) {
        messagingTemplate.convertAndSend("/topic/public/1", messageDto);
        this.messageService.saveMessage(messageDto, conversationId);
        headerAccessor.getSessionAttributes().put("username", messageDto.getSender());
        return messageDto;
    }
}
