package com.tragefox.api.exception;

import com.tragefox.api.exception.constant.ErrorType;
import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {

    private final ErrorType errorType;

    public ServerException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}
