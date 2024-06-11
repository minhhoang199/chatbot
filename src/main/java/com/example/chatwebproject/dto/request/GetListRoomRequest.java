package com.example.chatwebproject.dto.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class GetListRoomRequest {
    @NotNull
    @Min(1)
    private Long userId;
}
