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
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @GetMapping("")
    public ResponseEntity<BaseResponse> getLimitNotificationByUserId(@RequestParam(value = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before,
                                                                 @RequestParam(value = "limit", required = false) Integer limit){
        List<NotificationDto> notificationDtos = this.notificationService.getLimitNotificationByUserId(before, limit);
        return this.respFactory.success(notificationDtos);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<BaseResponse> unreadCount() {
        return this.respFactory.success(notificationService.countByUserIdAndIsReadFalse());
    }

    @GetMapping("/messages/unread-count")
    public ResponseEntity<BaseResponse> unreadMessagesCount(@RequestParam(value = "roomId", required = false) Long roomId) {
        return this.respFactory.success(notificationService.getByUserIdAndIsReadFalseAndMessageAddType(roomId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<BaseResponse> readNotification(@PathVariable("id") Long id) {
        return this.respFactory.success(notificationService.readNotification(id));
    }

    @PutMapping("/room-message/{roomId}/read")
    public ResponseEntity<BaseResponse> readRoomMessages(@PathVariable("roomId") Long roomId) {
        return this.respFactory.success(notificationService.readRoomMessages(roomId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteNotification(@PathVariable("id") Long id) {
        notificationService.deleteNotification(id);
        return this.respFactory.success();
    }
}
