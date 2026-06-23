package com.example.chatwebproject.model.request;

import com.example.chatwebproject.model.enums.Language;
import lombok.Data;

@Data
public class TranslateRequest {
    private String sourceLanguage;
    private String targetLanguage;
}
