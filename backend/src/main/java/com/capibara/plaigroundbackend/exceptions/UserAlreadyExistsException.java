package com.capibara.plaigroundbackend.exceptions;

import org.springframework.http.HttpStatusCode;

public class UserAlreadyExistsException extends AIPlaygroundException {
    public UserAlreadyExistsException() {
        super("User already exists", HttpStatusCode.valueOf(400));
    }
}
