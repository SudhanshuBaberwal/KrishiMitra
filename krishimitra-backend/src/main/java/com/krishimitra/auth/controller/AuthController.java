package com.krishimitra.auth.controller;

import com.krishimitra.auth.dto.request.LoginRequest;
import com.krishimitra.auth.dto.request.RegisterRequest;
import com.krishimitra.auth.dto.response.AuthResponse;
import com.krishimitra.auth.dto.response.MessageResponse;
import com.krishimitra.auth.dto.response.UserResponse;
import com.krishimitra.auth.security.UserPrincipal;
import com.krishimitra.auth.service.AuthService;
import com.krishimitra.common.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register a new farmer
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Account registered successfully",
                        response
                ));
    }

    // Login an existing user
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Login successful",
                        response
                )
        );
    }

    // Get currently authenticated user
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        UserResponse response = authService.getCurrentUser(principal);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User profile fetched successfully",
                        response
                )
        );
    }

    // Logout
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<MessageResponse>> logout() {

        MessageResponse response = new MessageResponse(
                "Logout successful. Please discard your access token."
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Logout successful",
                        response
                )
        );
    }
}