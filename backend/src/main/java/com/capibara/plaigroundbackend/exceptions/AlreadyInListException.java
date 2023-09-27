package com.capibara.plaigroundbackend.exceptions;

import org.springframework.http.HttpStatusCode;

public class AlreadyInListException extends AIPlaygroundException {

    public AlreadyInListException(String message, HttpStatusCode httpStatusCode) {
        super(message, httpStatusCode);
    }
}
