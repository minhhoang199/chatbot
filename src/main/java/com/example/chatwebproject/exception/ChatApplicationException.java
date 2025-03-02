package com.example.chatwebproject.exception;

import com.example.chatwebproject.constant.DomainCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class ChatApplicationException extends RuntimeException {
    private final DomainCode domainCode;
    private transient Object[] args;
    private HttpStatus status;

    public ChatApplicationException(DomainCode domainCode) {
        this.domainCode = domainCode;
    }

    public ChatApplicationException(DomainCode domainCode, Object[] args) {
        this.domainCode = domainCode;
        this.args = args;
    }

    public ChatApplicationException(HttpStatus status, DomainCode domainCode, Object[] args) {
        this.status = status;
        this.domainCode = domainCode;
        this.args = args;
    }

    public ChatApplicationException(HttpStatus status, DomainCode domainCode) {
        this.status = status;
        this.domainCode = domainCode;
    }
}

