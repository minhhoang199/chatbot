package com.example.chatwebproject.model.vm;

import com.example.chatwebproject.model.enums.MessageStatus;
import com.example.chatwebproject.model.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageVM {
    private String sender;
    private String content;
    private MessageType messageType;
    private MessageStatus messageStatus;
}
