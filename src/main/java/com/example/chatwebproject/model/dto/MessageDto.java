package com.example.chatwebproject.model.dto;

import com.example.chatwebproject.model.BaseEntity;
import com.example.chatwebproject.model.enums.MessageStatus;
import com.example.chatwebproject.model.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MessageDto extends BaseEntity {
    private Long id;
    private String sender; //Sender user name
    @NotBlank
    private Long senderId; //Sender user name
    @NotBlank
    private String content;
    @NotBlank
    private MessageType type;
    @NotBlank
    private MessageStatus messageStatus;
    @Size(min = 1)
    private Long roomId;
}
