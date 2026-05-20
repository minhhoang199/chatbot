package com.example.chatwebproject.controller;


import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.model.entity.Message;
import com.example.chatwebproject.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class ChatController {
    private MessageService messageService;

    @MessageMapping("/chat.sendMessage")
//    @SendTo("/topic/room")
    public MessageDto sendMessage(@Payload MessageDto chatMessage) {
        System.out.println(chatMessage);
        return this.messageService.saveMessage(chatMessage);
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public MessageDto addUser(@Payload MessageDto chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        MessageDto newMes = this.messageService.saveMessage(chatMessage);
        return newMes;
    }

}
