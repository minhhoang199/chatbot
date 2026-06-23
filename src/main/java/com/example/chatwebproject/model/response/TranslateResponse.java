package com.example.chatwebproject.model.response;

import com.example.chatwebproject.model.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslateResponse {
    private Long id;
    private String content;
    private String translatedText;
    private String sourceLanguage;
    private Language targetLanguage;
}
