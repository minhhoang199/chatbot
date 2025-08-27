package com.example.chatwebproject.model.entity;

import java.time.Instant;
import java.time.LocalDateTime;

public interface RoomProjection {
    Long getId();
    String getName();
    String getConversationType();
    String getLastMessageContent();
    Instant getLastMessageTime();
}
