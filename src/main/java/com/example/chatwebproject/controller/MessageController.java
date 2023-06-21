package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.service.MessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {
    private MessageService messageService;
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/conversations/{roomId}")
    public void handleMessage(@Payload MessageDto messageDto,
                                 @DestinationVariable Long roomId){
        messageService.saveMessage(messageDto, roomId);
        messagingTemplate.convertAndSend("/topic/messages/" + roomId, messageDto);
    }
}
