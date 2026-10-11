package com.tragefox.api.exception.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppErrorModel {
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
}
