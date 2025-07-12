package com.example.chatwebproject.model.response;


import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ChatApplicationException;
import com.example.chatwebproject.exception.ValidationRequestException;
import com.example.chatwebproject.model.dto.FieldErrorDto;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RespFactory {

    private final MessageSource messageSource;

    public ResponseEntity<RespBody> failWithDomainException(ChatApplicationException e) {
        RespBody responseBody = new RespBody();
        responseBody.setCode(e.getDomainCode().getCode());
        responseBody.setMessage(this.messageSource.getMessage(e.getDomainCode().getCode(), e.getArgs(), this.locale()));
        return ResponseEntity.status(e.getDomainCode().getStatus()).body(responseBody);
    }

    public ResponseEntity<RespBody> success(Object data) {
        RespBody responseBody = new RespBody();
        responseBody.setCode(DomainCode.SUCCESS.getCode());
        responseBody.setMessage(this.messageSource.getMessage(DomainCode.SUCCESS.getCode(), null, this.locale()));
        responseBody.setData(data);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    public ResponseEntity<RespBody> success(DomainCode domainCode, Object... args) {
        RespBody responseBody = new RespBody();
        responseBody.setCode(domainCode.getCode());
        responseBody.setMessage(this.messageSource.getMessage(domainCode.getCode(), args, this.locale()));
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    public ResponseEntity<RespBody> success(DomainCode domainCode) {
        RespBody responseBody = new RespBody();
        responseBody.setCode(domainCode.getCode());
        responseBody.setMessage(this.messageSource.getMessage(domainCode.getCode(), null, this.locale()));
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    public ResponseEntity<RespBody> success() {
        RespBody responseBody = new RespBody();
        responseBody.setCode(DomainCode.SUCCESS.getCode());
        responseBody.setMessage(this.messageSource.getMessage(DomainCode.SUCCESS.getCode(), null, this.locale()));
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    public ResponseEntity<RespBody> failWithValidationRequestException(ValidationRequestException ex) {
        RespBody responseBody = new RespBody();
        responseBody.setCode(ex.getDomainCode().getCode());
        responseBody.setMessage(this.messageSource.getMessage(ex.getDomainCode().getCode(), ex.getArgs(), this.locale()));
        responseBody.setFieldErrors(parseErrorData(ex.getException()));
        return ResponseEntity.status(ex.getStatus()).body(responseBody);
    }

    public ResponseEntity<RespBody> failWithInternalException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RespBody().withCode(DomainCode.INTERNAL_SERVICE_ERROR.getCode()).withMessage(messageSource.getMessage(DomainCode.INVALID_PARAMETER.getCode(), new Object[]{e.getMessage()}, this.locale())));
    }

    public ResponseEntity<RespBody> failWithBadInputParameter(Exception e) {
        Set<FieldErrorDto> fieldErrors = parseErrorData(e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RespBody().withCode(DomainCode.INVALID_PARAMETER.getCode()).withMessage(messageSource.getMessage(DomainCode.INVALID_PARAMETER.getCode(), null, this.locale())).withFieldErrors(fieldErrors));
    }

    public ResponseEntity<RespBody> failNotFoundData(Exception e) {
        Set<FieldErrorDto> fieldErrors = parseErrorData(e);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RespBody().withCode(DomainCode.NOT_FOUND_DATA.getCode()).withMessage(messageSource.getMessage(DomainCode.NOT_FOUND_DATA.getCode(), null, this.locale())).withFieldErrors(fieldErrors));
    }

    public ResponseEntity<RespBody> failWithMethodNotAllowInputParameter(HttpRequestMethodNotSupportedException e) {
        Set<FieldErrorDto> fieldErrors = parseErrorData(e);

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new RespBody().withCode(DomainCode.INVALID_PARAMETER.getCode()).withMessage(e.getMessage()).withFieldErrors(fieldErrors));
    }

    public Locale locale() {
        return LocaleContextHolder.getLocale();
    }

    private Set<FieldErrorDto> parseErrorData(Exception e) {
        Set<FieldErrorDto> errorSet = ConcurrentHashMap.newKeySet();

        if (e instanceof HttpMessageNotReadableException) {
            InvalidFormatException formatException = (InvalidFormatException) e.getCause();

            formatException.getPath().parallelStream().forEach(err -> {
                if (StringUtils.isNotEmpty(err.getFieldName())) {
                    FieldErrorDto errorDTO = new FieldErrorDto();
                    errorDTO.setErrorMessage(err.getFieldName());
                    errorDTO.setFieldName(formatException.getMessage());
                    errorSet.add(errorDTO);
                }
            });
        }

        if (e instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> set = ((ConstraintViolationException) e).getConstraintViolations();
            for (ConstraintViolation<?> next : set) {
                FieldErrorDto errorDTO = new FieldErrorDto();
                String field = String.valueOf(next.getPropertyPath());
                errorDTO.setErrorMessage(next.getMessage());
                errorDTO.setFieldName(field);
                errorSet.add(errorDTO);
            }
        }

        if (e instanceof MethodArgumentNotValidException) {

            List<FieldError> errors = ((MethodArgumentNotValidException) e).getFieldErrors();

            errors.parallelStream().forEach(err -> {
                FieldErrorDto errorDTO = new FieldErrorDto();
                errorDTO.setErrorMessage(err.getField());
                errorDTO.setFieldName(err.getDefaultMessage());
                errorSet.add(errorDTO);
            });
        }
        return errorSet;
    }

    public ResponseEntity<RespBody> failWithAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(RespBody.builder().code(String.valueOf(HttpServletResponse.SC_UNAUTHORIZED)).message(e.getMessage()).build());
    }
}

