package com.capibara.plaigroundbackend.exceptions;

import org.springframework.http.HttpStatusCode;

public class ServerException extends AIPlaygroundException {

    public ServerException(String reason) {
        super(reason, HttpStatusCode.valueOf(500));
    }

}
