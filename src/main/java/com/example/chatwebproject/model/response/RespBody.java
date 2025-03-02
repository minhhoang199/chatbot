package com.example.chatwebproject.model.response;

import com.example.chatwebproject.model.dto.FieldErrorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import java.util.Set;

@Setter
@Getter
@With
@AllArgsConstructor
@NoArgsConstructor
public class RespBody {
    private String code = "AS-000";

    private String message;

    private Object data;

    private Set<FieldErrorDto> fieldErrors;
}

