package com.casting.platform.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private String message;
    private String code;
    private HttpStatus status;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, String code, HttpStatus status) {
        this.message = message;
        this.code = code;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
