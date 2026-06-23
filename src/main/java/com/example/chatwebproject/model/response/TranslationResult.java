package com.example.chatwebproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TranslationResult {

    private String translatedText;

    private String sourceLanguage;
}
