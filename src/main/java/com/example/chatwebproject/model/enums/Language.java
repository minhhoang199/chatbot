package com.example.chatwebproject.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {

    ENGLISH("en"),
    VIETNAMESE("vi"),
    JAPANESE("ja"),
    KOREAN("ko"),
    CHINESE("zh");

    private final String code;
}
