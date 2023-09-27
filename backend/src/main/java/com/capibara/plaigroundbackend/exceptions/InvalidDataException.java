package com.capibara.plaigroundbackend.exceptions;

import org.springframework.http.HttpStatusCode;

public class InvalidDataException extends AIPlaygroundException {
    public InvalidDataException(String invalidData) {
        super(invalidData + " is invalid!", HttpStatusCode.valueOf(400));
    }
}
