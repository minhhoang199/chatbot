package com.example.chatwebproject.model.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestConnectionVM {
    @Pattern(regexp = "^0\\d{9}$|^84\\d{9}$", message = "Invalid following phone")
    private String followingPhone;
    @Pattern(regexp = "^0\\d{9}$|^84\\d{9}$", message = "Invalid followed phone")
    private String followedPhone;
}
