package com.capibara.plaigroundbackend.exceptions;

import org.springframework.http.HttpStatusCode;

public class DataRequiredException extends AIPlaygroundException {
    public DataRequiredException(String missingValue) {
        super("You are missing required field(s): " + missingValue, HttpStatusCode.valueOf(400));
    }
}
