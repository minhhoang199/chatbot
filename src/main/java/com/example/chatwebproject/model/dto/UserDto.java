package com.example.chatwebproject.model.dto;

import com.example.chatwebproject.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Name may not be blank")
    private String username;

    @Email
    private String email;

    private UserStatus status;
}
