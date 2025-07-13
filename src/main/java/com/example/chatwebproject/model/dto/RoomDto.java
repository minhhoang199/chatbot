package com.example.chatwebproject.model.dto;

import com.example.chatwebproject.model.enums.RoomType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private Long id;
    @NotBlank(message = "Room name may not be blank")
    private String name;
    @NotEmpty(message = "Email list may not be empty")
    @Size(min = 2, message = "Number of emails must be higher than 1")
    private List<String> emails;
    @NotNull
    private RoomType roomType;
    private String lastMessageContent;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastMessageTime;
    private String admin;
    private String privateKey;
}
