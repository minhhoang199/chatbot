package com.example.chatwebproject.model.vm;

import com.example.chatwebproject.model.enums.ConversationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PrivateConversationVM {
    @NotBlank(message = "Name may not be blank")
    private String name;
    @Pattern(regexp = "^0\\d{9}$|^84\\d{9}$", message = "Invalid invitor phone")
    private String invitorPhone;
    @Pattern(regexp = "^0\\d{9}$|^84\\d{9}$", message = "Invalid invitee phone")
    private String inviteePhone;
}
