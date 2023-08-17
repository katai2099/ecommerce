package com.web.ecommerce.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                             HttpServletRequest request
    ) {
        return getExceptionResponseResponseEntity(request, exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {InvalidContentException.class})
    public ResponseEntity<ExceptionResponse> handleInvalidContentException(InvalidContentException exception,
                                                                           HttpServletRequest request
    ) {
        return getExceptionResponseResponseEntity(request, exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ExceptionResponse> getExceptionResponseResponseEntity(HttpServletRequest request,
                                                                                 String message,
                                                                                 HttpStatus status) {
        ExceptionResponse error = new ExceptionResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setMessage(message);
        error.setStatus(status.value());
        error.setRequestURI(request.getRequestURI());
        return new ResponseEntity<>(error, status);
    }


}
