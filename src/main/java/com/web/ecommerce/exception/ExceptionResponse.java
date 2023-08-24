package com.web.ecommerce.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExceptionResponse {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String path;
}
