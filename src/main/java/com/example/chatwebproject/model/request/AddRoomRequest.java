package com.example.chatwebproject.model.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class AddRoomRequest {
    @NotNull
    @Valid
    private SaveRoomRequest saveRoomRequest;
}
