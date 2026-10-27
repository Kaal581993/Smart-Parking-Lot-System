package com.smart.parking.lot.smart.parking.lot.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleFieldValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                e -> errors.put(e.getField(), e.getDefaultMessage()));
        errors.put("error", errors.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .sorted().collect(java.util.stream.Collectors.joining("; ")));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({ConstraintViolationException.class, TransactionSystemException.class})
    public ResponseEntity<Map<String, String>> handlePersistenceValidation(Exception ex) {
        Throwable cause = ex;
        while (cause != null) {
            if (cause instanceof ConstraintViolationException validation) {
                Map<String, String> errors = new HashMap<>();
                validation.getConstraintViolations().forEach(violation ->
                        errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
                errors.put("error", validation.getConstraintViolations().stream()
                        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                        .sorted().collect(java.util.stream.Collectors.joining("; ")));
                return ResponseEntity.badRequest().body(errors);
            }
            cause = cause.getCause();
        }
        return handleGeneric(ex);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(
            IllegalArgumentException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableJson(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid request JSON. Check the field types and required values."));
    }

    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleOversizedUpload(Exception ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Map.of("error", "Profile image must be 2 MB or smaller."));
    }

    @ExceptionHandler({jakarta.persistence.EntityNotFoundException.class,
            java.util.NoSuchElementException.class})
    public ResponseEntity<Map<String, String>> handleElementNotFound(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(org.springframework.web.server.ResponseStatusException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(org.springframework.dao.DataIntegrityViolationException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Data integrity violation: " + ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        if (ex instanceof org.springframework.web.ErrorResponse error) {
            String detail = error.getBody().getDetail();
            return ResponseEntity.status(error.getStatusCode())
                    .body(Map.of("error", detail == null ? "Request could not be completed" : detail));
        }
        log.error("Unhandled request failure", ex);
        Map<String, String> body = new HashMap<>();
        body.put("error", "An unexpected server error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
