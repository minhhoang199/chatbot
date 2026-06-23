package com.example.chatwebproject.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibreTranslateRequest {

    private String q;

    private String source;

    private String target;
}
