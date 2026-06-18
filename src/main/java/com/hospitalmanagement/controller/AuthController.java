package com.hospitalmanagement.controller;

import com.hospitalmanagement.dto.request.ChangePasswordRequest;
import com.hospitalmanagement.dto.request.LoginRequest;
import com.hospitalmanagement.dto.request.RefreshTokenRequest;
import com.hospitalmanagement.dto.request.RegisterRequest;
import com.hospitalmanagement.dto.response.AuthResponse;
import com.hospitalmanagement.dto.response.UserResponse;
import com.hospitalmanagement.exception.GlobalExceptionHandler.ApiResponse;
import com.hospitalmanagement.security.SecurityUtils;
import com.hospitalmanagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.success("Login successful", response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ApiResponse.success("Registration successful", response);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password on first login")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String username = SecurityUtils.getCurrentUserUsername();
        authService.changePassword(username, request);
        return ApiResponse.success("Password changed successfully", null);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<ApiResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ApiResponse.success("Token refreshed successfully", response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user details")
    public ResponseEntity<ApiResponse> getCurrentUser() {
        String username = SecurityUtils.getCurrentUserUsername();
        UserResponse response = authService.getCurrentUser(username);
        return ApiResponse.success("Current user retrieved", response);
    }
}
