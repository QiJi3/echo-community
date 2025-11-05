package com.echo.api;

import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<Result<Object>> handleApiException(ApiException ex) {
    if (ex.getStatus() >= 500) {
      log.error("Application error", ex);
    }
    return ResponseEntity.ok(Result.fail(ex.getCode(), defaultMessage(ex.getMessage(), "Request failed")));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Result<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    return ResponseEntity.ok(Result.fail(400001, resolveFieldErrorMessage(ex.getBindingResult().getFieldErrors())));
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<Result<Object>> handleBindException(BindException ex) {
    return ResponseEntity.ok(Result.fail(400001, resolveFieldErrorMessage(ex.getBindingResult().getFieldErrors())));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Result<Object>> handleConstraintViolation(ConstraintViolationException ex) {
    String message = "Validation failed";
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      message = violation.getMessage();
      break;
    }
    return ResponseEntity.ok(Result.fail(400001, message));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Result<Object>> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
    return ResponseEntity.ok(Result.fail(400001, ex.getParameterName() + " is required"));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Result<Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return ResponseEntity.ok(Result.fail(400001, ex.getName() + " has an invalid value"));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Result<Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    return ResponseEntity.ok(Result.fail(400001, "Request body must be a valid JSON object"));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<Result<Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
    return ResponseEntity.ok(Result.fail(404001, "Endpoint not found"));
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<Result<Object>> handleNoHandlerFound(NoHandlerFoundException ex) {
    return ResponseEntity.ok(Result.fail(404001, "Endpoint not found"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Result<Object>> handleException(Exception ex) {
    log.error("Unexpected server error", ex);
    return ResponseEntity.ok(Result.fail(500001, "Internal server error"));
  }

  private String resolveFieldErrorMessage(List<FieldError> fieldErrors) {
    if (fieldErrors == null || fieldErrors.isEmpty()) {
      return "Validation failed";
    }

    FieldError fieldError = fieldErrors.get(0);
    String message = defaultMessage(fieldError.getDefaultMessage(), "Validation failed");
    log.warn("Validation error: field={}, message={}", fieldError.getField(), message);
    return message;
  }

  private String defaultMessage(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }
}
