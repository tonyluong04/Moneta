package com.networthtracker.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

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

    /**
     * A body jackson could not parse at all, so @Valid never got to run. The common
     * case is an unknown enum name — a category of "SAVINGS" rather than "CASH" —
     * which otherwise surfaces as spring's default error body and reaches the
     * frontend as a bare "Request failed".
     *
     * InvalidFormatException comes from tools.jackson, not com.fasterxml.jackson:
     * spring boot 4 ships jackson 3. The old 2.x package is still on the classpath
     * (java-jwt pulls it in), so importing the wrong one compiles happily and then
     * never matches at runtime.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableBody(
            HttpMessageNotReadableException ex) {
        String message = "Request body could not be read";

        if (ex.getCause() instanceof InvalidFormatException cause) {
            Class<?> targetType = cause.getTargetType();
            if (targetType != null && targetType.isEnum()) {
                message = "\"" + cause.getValue() + "\" is not a valid "
                        + targetType.getSimpleName() + " value";
            } else {
                message = "Invalid value: " + cause.getValue();
            }
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
