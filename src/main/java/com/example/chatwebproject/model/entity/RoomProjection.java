package com.example.chatwebproject.model.entity;

import com.example.chatwebproject.model.enums.RoomStatus;

import java.time.Instant;
import java.time.LocalDateTime;

public interface RoomProjection {
    Long getId();
    String getName();
    String getConversationType();
    String getLastMessageContent();
    Instant getLastMessageTime();
    RoomStatus getStatus();
}
