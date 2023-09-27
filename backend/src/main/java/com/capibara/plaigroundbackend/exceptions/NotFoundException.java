package com.capibara.plaigroundbackend.exceptions;

import org.springframework.http.HttpStatusCode;

public class NotFoundException extends AIPlaygroundException {
    public NotFoundException(String missingItem) {
        super(missingItem + " not found", HttpStatusCode.valueOf(404));
    }
}
