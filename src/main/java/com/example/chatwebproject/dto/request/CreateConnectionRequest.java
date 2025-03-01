package com.example.chatwebproject.dto.request;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class CreateConnectionRequest {
    @Pattern(regexp = "^0\\d{9}$|^84\\d{9}$", message = "Invalid following phone")
    private String requestPhone;
    @Pattern(regexp = "^0\\d{9}$|^84\\d{9}$", message = "Invalid followed phone")
    private String acceptedPhone;
}
