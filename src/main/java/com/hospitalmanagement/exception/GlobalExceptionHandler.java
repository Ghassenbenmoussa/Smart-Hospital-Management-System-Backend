package com.hospitalmanagement.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse> handleBadRequest(BadRequestException ex) {
        return ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse> handleUnauthorized(UnauthorizedException ex) {
        return ApiResponse.error(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ApiResponse response = ApiResponse.builder()
                .success(false)
                .message("Validation failed")
                .errors(errors)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDenied(AccessDeniedException ex) {
        return ApiResponse.error("Access denied", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneric(Exception ex) {
        return ApiResponse.error("An unexpected error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
        private Map<String, String> errors;

        public ApiResponse() {
        }

        public ApiResponse(boolean success, String message, Object data, Map<String, String> errors) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.errors = errors;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static ResponseEntity<ApiResponse> success(String message, Object data) {
            ApiResponse response = new ApiResponse(true, message, data, null);
            return ResponseEntity.ok(response);
        }

        public static ResponseEntity<ApiResponse> error(String message, HttpStatus status) {
            ApiResponse response = new ApiResponse(false, message, null, null);
            return new ResponseEntity<>(response, status);
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public Map<String, String> getErrors() {
            return errors;
        }

        public void setErrors(Map<String, String> errors) {
            this.errors = errors;
        }

        public static class Builder {
            private boolean success;
            private String message;
            private Object data;
            private Map<String, String> errors;

            Builder() {
            }

            public Builder success(boolean success) {
                this.success = success;
                return this;
            }

            public Builder message(String message) {
                this.message = message;
                return this;
            }

            public Builder data(Object data) {
                this.data = data;
                return this;
            }

            public Builder errors(Map<String, String> errors) {
                this.errors = errors;
                return this;
            }

            public ApiResponse build() {
                return new ApiResponse(success, message, data, errors);
            }
        }
    }
}
