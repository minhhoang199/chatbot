package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.model.response.BaseResponse;
import com.example.chatwebproject.model.response.RespFactory;
import com.example.chatwebproject.service.MessageService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/api/v1/messages")
@AllArgsConstructor
public class MessageController {
    private MessageService messageService;
    private RespFactory respFactory;
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("")
    public ResponseEntity<BaseResponse> getAllMessagesByRoomId(@RequestParam("roomId") Long roomId,
                                                               @RequestParam("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before,
                                                               @RequestParam("limit") Integer limit){
        if (ObjectUtils.isEmpty(limit) || limit <= 0) limit = 50;
        List<MessageDto> messageDtoList = this.messageService.getAllMessages(roomId, before, limit);
        return this.respFactory.success(messageDtoList);
    }

    @PutMapping("")
    public ResponseEntity<BaseResponse> update(@RequestBody @Valid MessageDto messageDto){
        MessageDto dto = this.messageService.editMessage(messageDto);
        String destination = "/topic/room/" + dto.getRoomId();
        messagingTemplate.convertAndSend(destination, dto);
        return this.respFactory.success();
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<BaseResponse> deleteMessage(@PathVariable("messageId") Long messageId){
        MessageDto dto = this.messageService.deactiveMessage(messageId);
        String destination = "/topic/room/" + dto.getRoomId();
        messagingTemplate.convertAndSend(destination, dto);
        return this.respFactory.success();
    }
}
