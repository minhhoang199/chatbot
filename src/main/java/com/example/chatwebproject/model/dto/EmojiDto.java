package com.example.chatwebproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmojiDto implements Serializable {
    private long userId;
    private String username;
    private String emoji;
}
