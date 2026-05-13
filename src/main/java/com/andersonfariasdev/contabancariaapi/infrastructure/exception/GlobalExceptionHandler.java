package com.andersonfariasdev.contabancariaapi.infrastructure.exception;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    record ApiError(String message, int status, OffsetDateTime timestamp) {
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ApiError> handleRateLimit(RequestNotPermitted ex) {
        ApiError err = new ApiError("Rate limit exceeded: " + ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS.value(), OffsetDateTime.now());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(err);
    }
}
