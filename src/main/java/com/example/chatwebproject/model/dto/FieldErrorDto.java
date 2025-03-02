package com.example.chatwebproject.model.dto;

import lombok.Data;

@Data
public class FieldErrorDto {
    private String fieldName;
    private String errorMessage;
}

