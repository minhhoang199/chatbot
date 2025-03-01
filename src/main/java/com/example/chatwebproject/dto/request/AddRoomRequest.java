package com.example.chatwebproject.dto.request;

import com.example.chatwebproject.model.dto.SaveRoomRequest;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class AddRoomRequest {
    @NotNull
    @Valid
    private SaveRoomRequest saveRoomRequest;

    private Boolean isPrivateChat;
}
