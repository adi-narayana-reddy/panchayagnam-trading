package com.py.controller.utils;

import com.py.brokers.angel.exception.SmartAPIException;
import com.py.common.dto.ApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LogManager.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private ControllerResponseBuilder responseBuilder;

    private String safeMessage(Throwable ex) {
        String msg = ex.getMessage();
        return (msg == null || msg.isBlank()) ? "An unexpected error occurred" : msg;
    }

    @ExceptionHandler(SmartAPIException.class)
    public ResponseEntity<ApiResponse<?>> handleSmartApiException(SmartAPIException ex) {
        log.error("SmartAPIException: {}", safeMessage(ex), ex);
        // Broker API errors map to 502 (Bad gateway) with a safe message
        return responseBuilder.buildError(502, "Broker API error: " + safeMessage(ex));
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<ApiResponse<?>> handleIOException(IOException ex) {
        log.error("IOException: {}", safeMessage(ex), ex);
        return responseBuilder.buildError(502, "IO error: " + safeMessage(ex));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiResponse<?>> handleBadRequest(RuntimeException ex) {
        log.warn("Bad request: {}", safeMessage(ex));
        return responseBuilder.buildError(400, safeMessage(ex));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", safeMessage(ex));
        return responseBuilder.buildError(403, "Access denied: " + safeMessage(ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        String msg = errors.stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Validation failed: {}", msg);
        return responseBuilder.buildError(400, "Validation failed: " + msg);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntime(RuntimeException ex) {
        log.error("RuntimeException: {}", safeMessage(ex), ex);
        return responseBuilder.buildError(500, "Internal server error: " + safeMessage(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneric(Exception ex) {
        log.error("Unhandled exception: {}", safeMessage(ex), ex);
        return responseBuilder.buildError(500, "Internal server error: " + safeMessage(ex));
    }
}
