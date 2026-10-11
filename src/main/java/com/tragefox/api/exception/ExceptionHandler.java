package com.tragefox.api.exception;

import com.tragefox.api.exception.constant.ErrorType;
import com.tragefox.api.exception.model.AppErrorModel;
import com.tragefox.api.exception.model.ServerErrorModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @org.springframework.web.bind.annotation.ExceptionHandler(ServerException.class)
    public ResponseEntity<ServerErrorModel> handle(ServerException baseException, HttpServletRequest request) {
        ErrorType errorType = baseException.getErrorType();
        return ResponseEntity.status(errorType.getCode())
                .body(ServerErrorModel.builder()
                        .errorCode(errorType.getErrorCode())
                        .timestamp(LocalDateTime.now())
                        .message(messageSource.getMessage(errorType.getMessage(), null, Locale.ENGLISH))
                        .uri(request.getRequestURI())
                        .build());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({ApplicationException.class, AccessDeniedException.class})
    public ResponseEntity<AppErrorModel> handle(ApplicationException applicationException) {
        ErrorType errorType = applicationException.getErrorType();
        return ResponseEntity.status(applicationException.getErrorType().getCode())
                .body(AppErrorModel.builder()
                        .errorCode(errorType.getErrorCode())
                        .timestamp(LocalDateTime.now())
                        .message(messageSource.getMessage(errorType.getMessage(), null, Locale.ENGLISH))
                        .build());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
        ConstraintViolationException.class,
        RuntimeException.class
    })
    public ResponseEntity<AppErrorModel> handle(RuntimeException runtimeException) {
        ErrorType errorType = ErrorType.INTERNAL_ERROR;
        return ResponseEntity.status(errorType.getCode())
                .body(AppErrorModel.builder()
                        .errorCode(errorType.getErrorCode())
                        .timestamp(LocalDateTime.now())
                        .message(runtimeException.getLocalizedMessage())
                        .build());
    }
}
