package com.krishimitra.common;

import com.krishimitra.auth.exception.EmailAlreadyExistsException;
import com.krishimitra.common.response.ApiResponse;
import com.krishimitra.common.response.ErrorResponse;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleValidationException(
            MethodArgumentNotValidException exception
    ) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage()
                )
        );

        ErrorResponse errorResponse = new ErrorResponse(
                "Validation failed",
                fieldErrors
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Please check the submitted fields",
                errorResponse
        );
    }

    // Duplicate email
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDuplicateEmail(
            EmailAlreadyExistsException exception
    ) {

        ErrorResponse errorResponse = new ErrorResponse(
                "EMAIL_ALREADY_EXISTS",
                Map.of()
        );

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                errorResponse
        );
    }

    // MongoDB unique index conflict
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDuplicateKey(
            DuplicateKeyException exception
    ) {

        ErrorResponse errorResponse = new ErrorResponse(
                "DUPLICATE_RESOURCE",
                Map.of()
        );

        return buildResponse(
                HttpStatus.CONFLICT,
                "A record with the same unique value already exists",
                errorResponse
        );
    }

    // Invalid login credentials or authentication failure
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAuthenticationException(
            AuthenticationException exception
    ) {

        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_CREDENTIALS",
                Map.of()
        );

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password",
                errorResponse
        );
    }

    // Invalid JSON request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleInvalidRequestBody(
            HttpMessageNotReadableException exception
    ) {

        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_REQUEST_BODY",
                Map.of()
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Request body is missing or invalid",
                errorResponse
        );
    }

    // Fallback for unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleGeneralException(
            Exception exception
    ) {

        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                Map.of()
        );

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Please try again later.",
                errorResponse
        );
    }

    // Common response builder
    private ResponseEntity<ApiResponse<ErrorResponse>> buildResponse(
            HttpStatus status,
            String message,
            ErrorResponse errorResponse
    ) {

        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(message, errorResponse));
    }
}