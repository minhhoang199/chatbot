package com.example.chatwebproject.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class UserDto {
    @NotBlank(message = "Name may not be blank")
    private String username;

    @Email
    private String email;
}
