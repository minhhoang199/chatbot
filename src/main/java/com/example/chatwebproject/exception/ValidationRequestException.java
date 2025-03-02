package com.example.chatwebproject.exception;


import com.example.chatwebproject.constant.DomainCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintViolationException;

@Getter
public class ValidationRequestException extends ChatApplicationException {

    private final ConstraintViolationException exception;

    public ValidationRequestException(DomainCode code, Object[] args,
                                      ConstraintViolationException exception) {
        super(HttpStatus.valueOf(code.getStatus()), code, args);
        this.exception = exception;
    }

}

