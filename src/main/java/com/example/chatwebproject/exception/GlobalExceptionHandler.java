package com.example.chatwebproject.exception;

import com.example.chatwebproject.model.response.BaseResponse;
import com.example.chatwebproject.model.response.RespFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final RespFactory responseFactory;

    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<BaseResponse> handleIllegalArgumentException(AuthenticationException ex) {
        return responseFactory.failWithAuthenticationException(ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return responseFactory.failWithBadInputParameter(ex);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentNotValid(HttpRequestMethodNotSupportedException ex) {
        return responseFactory.failWithMethodNotAllowInputParameter(ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        return responseFactory.failWithBadInputParameter(ex);
    }

    @ExceptionHandler(ChatApplicationException.class)
    public ResponseEntity<BaseResponse> handleApprovalException(ChatApplicationException ex) {
        return responseFactory.failWithDomainException(ex);
    }

    @ExceptionHandler(ValidationRequestException.class)
    public ResponseEntity<BaseResponse> handleValidationRequestException(ValidationRequestException ex) {
        return responseFactory.failWithValidationRequestException(ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return responseFactory.failWithInternalException(ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<BaseResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return responseFactory.failWithInternalException(ex);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<BaseResponse> handleIllegalArgumentException(Exception ex) {
        return responseFactory.failWithInternalException(ex);
    }
}

