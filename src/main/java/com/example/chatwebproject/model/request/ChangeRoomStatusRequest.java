package com.example.chatwebproject.model.request;

import com.example.chatwebproject.model.enums.FriendshipStatus;
import com.example.chatwebproject.model.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeRoomStatusRequest {
    @Min(1)
    private Long id;
    @NotNull(message = "Status may not be null")
    private RoomStatus roomStatus;
}
