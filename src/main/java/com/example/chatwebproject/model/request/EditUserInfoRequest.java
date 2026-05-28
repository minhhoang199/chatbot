package com.example.chatwebproject.model.request;

import com.example.chatwebproject.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EditUserInfoRequest {
    private Long id;

    @NotBlank(message = "Name may not be blank")
    private String username;
}
