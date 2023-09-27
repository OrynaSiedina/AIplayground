package com.capibara.plaigroundbackend.exceptions;

import org.springframework.http.HttpStatusCode;

public class AccessForbiddenException extends AIPlaygroundException {

    public AccessForbiddenException(String message) {
        super(message, HttpStatusCode.valueOf(403));
    }

    public AccessForbiddenException() {
        super("Access Forbidden", HttpStatusCode.valueOf(403));
    }

}
