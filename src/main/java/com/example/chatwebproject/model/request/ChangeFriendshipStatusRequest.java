package com.example.chatwebproject.model.request;

import com.example.chatwebproject.model.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeFriendshipStatusRequest {
    @Min(1)
    private Long id;
    @NotNull(message = "Status may not be null")
    private FriendshipStatus friendshipStatus;
}
