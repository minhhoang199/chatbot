package com.example.chatwebproject.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LibreTranslateResponse {

    private ResponseData responseData;

    private Integer responseStatus;

    @Data
    @NoArgsConstructor
    public static class ResponseData {

        private String translatedText;

        private Double match;
    }
}
