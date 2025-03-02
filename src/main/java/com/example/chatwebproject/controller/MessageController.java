package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.model.response.Result;
import com.example.chatwebproject.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/v1/messages")
@AllArgsConstructor
public class MessageController {
    private MessageService messageService;
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/{roomId}")
    public ResponseEntity<Result> getAllMessagesByRoomId(@PathVariable("roomId") Long roomId){
        List<MessageDto> messageDtoList = this.messageService.getAllMessages(roomId);
        return ResponseEntity.ok(new Result("201", "Success", messageDtoList));
    }
}
