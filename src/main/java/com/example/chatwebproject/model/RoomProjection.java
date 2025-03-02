package com.example.chatwebproject.model;

import java.time.LocalDateTime;

public interface RoomProjection {
    Long getId();
    String getName();
    String getConversationType();
    String getLastMessageContent();
    LocalDateTime getLastMessageTime();
}
