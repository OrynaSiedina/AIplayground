package com.capibara.plaigroundbackend.models.dtos;

import lombok.Data;
import org.springframework.http.HttpStatusCode;

import java.util.Date;

@Data
public class ErrorDTO {
    private HttpStatusCode statusCode;
    private String message;
    private Date timestamp = new Date();

    public ErrorDTO(HttpStatusCode statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
