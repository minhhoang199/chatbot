package com.example.chatwebproject.model.enums;

import java.awt.*;

public enum RoomType {
    PRIVATE_CHAT,
    GROUP_CHAT;

    public static RoomType fromString(String colorString) {
        if (colorString == null) {
            throw new IllegalArgumentException("RoomType string cannot be null");
        }
        try {
            return RoomType.valueOf(colorString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No enum constant for RoomType: " + colorString);
        }
    }
}
