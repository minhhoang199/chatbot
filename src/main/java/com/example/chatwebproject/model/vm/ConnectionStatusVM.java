package com.example.chatwebproject.model.vm;

import com.example.chatwebproject.model.enums.ConnectionStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionStatusVM {
    @Pattern(regexp = "^0\\d{9}$|^84\\d{9}$", message = "Invalid following phone")
    private String followingPhone;
    @Pattern(regexp = "^0\\d{9}$|^84\\d{9}$", message = "Invalid followed phone")
    private String followedPhone;
    @NotNull(message = "Connection status may not be null")
    private ConnectionStatus connectionStatus;
}
