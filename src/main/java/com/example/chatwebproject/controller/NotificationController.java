package com.example.chatwebproject.controller;

import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.model.dto.NotificationDto;
import com.example.chatwebproject.model.response.BaseResponse;
import com.example.chatwebproject.model.response.RespFactory;
import com.example.chatwebproject.service.NotificationService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.List;

@Controller
@RequestMapping("/api/v1/notifications")
@AllArgsConstructor
public class NotificationController {
    private NotificationService notificationService;
    private RespFactory respFactory;

    @GetMapping("/limit")
    public ResponseEntity<BaseResponse> getLimitNotificationByUserId(@RequestParam("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before,
                                                                 @RequestParam("limit") Integer limit){
        if (ObjectUtils.isEmpty(limit) || limit <= 0) limit = 50;
        List<MessageDto> messageDtoList = this.notificationService.getLimitNotificationByUserId(before, limit);
        return this.respFactory.success(messageDtoList);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<BaseResponse> unreadCount() {
        return this.respFactory.success(notificationService.countByUserIdAndIsReadFalse());
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<BaseResponse> readNotification(@PathVariable("id") Long id) {
        return this.respFactory.success(notificationService.readNotification(id));
    }
}
