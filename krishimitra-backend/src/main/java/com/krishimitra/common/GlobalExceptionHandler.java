
package com.krishimitra.common;

import com.krishimitra.advisory.exception.AdvisoryNotFoundException;
import com.krishimitra.advisory.exception.InvalidAdvisoryAccessException;
import com.krishimitra.auth.exception.EmailAlreadyExistsException;
import com.krishimitra.common.response.ErrorResponse;
import com.krishimitra.crop.exception.CropNotFoundException;
import com.krishimitra.crop.exception.DuplicateFarmerCropException;
import com.krishimitra.crop.exception.FarmerProfileNotFoundException;

import com.krishimitra.schedule.exception.ScheduleCropNotFoundException;
import com.krishimitra.schedule.exception.ScheduleNotFoundException;
import com.krishimitra.soil.exception.SoilFarmerProfileNotFoundException;
import com.krishimitra.soil.exception.SoilRecordNotFoundException;
import com.krishimitra.weather.exception.WeatherDataException;
import com.krishimitra.weather.exception.WeatherLocationNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. Validation errors
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
                "VALIDATION_FAILED",
                fieldErrors
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Please check the submitted fields",
                errorResponse
        );
    }

    // 2. Duplicate email
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDuplicateEmail(
            EmailAlreadyExistsException exception
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                new ErrorResponse("EMAIL_ALREADY_EXISTS", Map.of())
        );
    }

    // 3. Farmer profile not found
    @ExceptionHandler(FarmerProfileNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleFarmerProfileNotFound(
            FarmerProfileNotFoundException exception
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                new ErrorResponse("FARMER_PROFILE_NOT_FOUND", Map.of())
        );
    }

    // 4. Crop not found
    @ExceptionHandler(CropNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleCropNotFound(
            CropNotFoundException exception
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                new ErrorResponse("CROP_NOT_FOUND", Map.of())
        );
    }

    // 5. Duplicate crop
    @ExceptionHandler(DuplicateFarmerCropException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDuplicateCrop(
            DuplicateFarmerCropException exception
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                new ErrorResponse("DUPLICATE_CROP", Map.of())
        );
    }

    // 6. MongoDB unique index conflict
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDuplicateKey(
            DuplicateKeyException exception
    ) {
        log.warn("MongoDB duplicate key conflict", exception);

        return buildResponse(
                HttpStatus.CONFLICT,
                "A record with the same unique value already exists",
                new ErrorResponse("DUPLICATE_RESOURCE", Map.of())
        );
    }

    // 7. Authentication failure
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAuthenticationException(
            AuthenticationException exception
    ) {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password",
                new ErrorResponse("INVALID_CREDENTIALS", Map.of())
        );
    }

    // 8. Invalid JSON request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleInvalidRequestBody(
            HttpMessageNotReadableException exception
    ) {
        log.warn("Invalid request body: {}", exception.getMessage());

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Request body is missing or invalid",
                new ErrorResponse("INVALID_REQUEST_BODY", Map.of())
        );
    }

    // 9. Fallback for unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleGeneralException(
            Exception exception
    ) {
        log.error("Unhandled API exception", exception);

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Please try again later.",
                new ErrorResponse("INTERNAL_SERVER_ERROR", Map.of())
        );
    }

    @ExceptionHandler(SoilRecordNotFoundException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleSoilRecordNotFound(
            SoilRecordNotFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error(
                        ex.getMessage(),
                        Map.of("error", "SOIL_RECORD_NOT_FOUND")
                )
        );
    }

    @ExceptionHandler(SoilFarmerProfileNotFoundException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleSoilFarmerNotFound(
            SoilFarmerProfileNotFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error(
                        ex.getMessage(),
                        Map.of("error", "FARMER_PROFILE_NOT_FOUND")
                )
        );
    }

    @ExceptionHandler(WeatherLocationNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleWeatherLocationNotFound(
            WeatherLocationNotFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(WeatherDataException.class)
    public ResponseEntity<ApiResponse<Object>> handleWeatherDataException(
            WeatherDataException ex
    ) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(AdvisoryNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleAdvisoryNotFound(
            AdvisoryNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidAdvisoryAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidAdvisoryAccess(
            InvalidAdvisoryAccessException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(
            IllegalArgumentException ex) {

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage(), null));
    }


    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleScheduleNotFound(
            ScheduleNotFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(ScheduleCropNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleScheduleCropNotFound(
            ScheduleCropNotFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), null));
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
