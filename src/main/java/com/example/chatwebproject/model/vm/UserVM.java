package com.example.chatwebproject.model.vm;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class UserVM {
    @NotBlank(message = "Name may not be blank")
    private String username;

    @Pattern(regexp = "^0\\d{9}$|^84\\d{9}$", message = "Invalid phone")
    private String phone;
}
