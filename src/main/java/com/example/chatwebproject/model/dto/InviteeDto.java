package com.example.chatwebproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InviteeDto {
    @NotEmpty
    @Size(min = 1, message = "Number of emails must be higher than 0")
    private List<String> inviteeEmails;
    @Email
    private String invitorEmail;
}
