package com.example.chatwebproject.dto.request;

import com.example.chatwebproject.model.dto.RoomDto;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class AddRoomRequest {
    @NotNull
    @Valid
    private RoomDto roomDto;
}
