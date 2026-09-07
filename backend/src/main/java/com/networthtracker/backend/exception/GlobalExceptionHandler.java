package com.networthtracker.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// turns exceptions from any controller into the {"error": "..."} shape the frontend reads.
// without this, a @Valid failure escapes to /error, gets blocked by spring security,
// and the browser only sees an empty 403.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @Valid failures — report the first field message,
    // e.g. "Password must be at least 8 characters"
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationError(
            MethodArgumentNotValidException ex) {
        String message = "Invalid request";

        FieldError firstFieldError = ex.getBindingResult().getFieldError();
        if (firstFieldError != null && firstFieldError.getDefaultMessage() != null) {
            message = firstFieldError.getDefaultMessage();
        }

        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    // fallback for any controller that doesn't declare its own handler
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Invalid request";
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }
}
