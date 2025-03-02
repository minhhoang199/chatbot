package com.example.chatwebproject.model.request;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class CreateConnectionRequest {
    @Email
    private String requestEmail;
    @Email
    private String acceptedEmail;
}
