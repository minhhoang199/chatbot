package com.example.chatwebproject.model.request;

import com.example.chatwebproject.model.enums.OTPType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OTPGenerateRequest {
    private String email;
    private OTPType otpType;
    private String newPassword;
}
