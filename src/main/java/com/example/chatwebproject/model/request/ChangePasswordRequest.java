package com.example.chatwebproject.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    private String oldPassword;
    @NotEmpty
    private String newPassword;
}
