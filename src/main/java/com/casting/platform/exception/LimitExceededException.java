package com.casting.platform.exception;

public class LimitExceededException extends BusinessException {
    public LimitExceededException(String message) {
        super(message);
    }
}
