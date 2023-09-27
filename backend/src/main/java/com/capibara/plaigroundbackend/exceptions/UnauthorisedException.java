package com.capibara.plaigroundbackend.exceptions;

import org.springframework.http.HttpStatusCode;

public class UnauthorisedException extends AIPlaygroundException {

    public UnauthorisedException() {
        super("Unauthorized, please login again!", HttpStatusCode.valueOf(401));
    }

}
