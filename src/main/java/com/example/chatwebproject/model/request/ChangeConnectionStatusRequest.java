package com.example.chatwebproject.model.request;

import com.example.chatwebproject.model.enums.ConnectionStatus;
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
public class ChangeConnectionStatusRequest {
    @Min(1)
    private Long connectionId;
    @NotNull(message = "Connection status may not be null")
    private ConnectionStatus connectionStatus;
}
