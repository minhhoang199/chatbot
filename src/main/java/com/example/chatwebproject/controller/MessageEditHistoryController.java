package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.model.dto.MessageEditHistoryDto;
import com.example.chatwebproject.model.entity.MessageEditHistory;
import com.example.chatwebproject.model.response.BaseResponse;
import com.example.chatwebproject.model.response.RespFactory;
import com.example.chatwebproject.service.MessageEditHistoryService;
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

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;

@Controller
@RequestMapping("/api/v1/message-edit-history")
@AllArgsConstructor
public class MessageEditHistoryController {
    private MessageEditHistoryService messageEditHistoryService;
    private RespFactory respFactory;

    @GetMapping("/{messageId}")
    public ResponseEntity<BaseResponse> getByMessageId(@PathVariable("messageId") Long messageId){
        List<MessageEditHistoryDto> messageEditHistoryDtos = this.messageEditHistoryService.getEditHistory(messageId);
        return this.respFactory.success(messageEditHistoryDtos);
    }
}
