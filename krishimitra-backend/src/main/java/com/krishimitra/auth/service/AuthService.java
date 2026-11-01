package com.krishimitra.auth.service;

import com.krishimitra.auth.dto.request.LoginRequest;
import com.krishimitra.auth.dto.request.RegisterRequest;
import com.krishimitra.auth.dto.response.AuthResponse;
import com.krishimitra.auth.dto.response.UserResponse;
import com.krishimitra.auth.exception.EmailAlreadyExistsException;
import com.krishimitra.auth.model.User;
import com.krishimitra.auth.model.UserRole;
import com.krishimitra.auth.repository.UserRepository;
import com.krishimitra.auth.security.JwtService;
import com.krishimitra.auth.security.UserPrincipal;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Register a new farmer
    public AuthResponse register(RegisterRequest request) {

        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException(
                    "An account with this email already exists"
            );
        }

        Instant now = Instant.now();

        User user = new User();

        user.setName(request.name().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.password()));

        // Public registration always creates a FARMER account.
        user.setRole(UserRole.FARMER);

        user.setEnabled(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        try {
            user = userRepository.save(user);
        } catch (DuplicateKeyException exception) {
            // Handles duplicate registrations occurring concurrently.
            throw new EmailAlreadyExistsException(
                    "An account with this email already exists"
            );
        }

        return buildAuthResponse(user);
    }

    // Authenticate an existing user
    public AuthResponse login(LoginRequest request) {

        String normalizedEmail = normalizeEmail(request.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizedEmail,
                        request.password()
                )
        );

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user could not be found"
                ));

        return buildAuthResponse(user);
    }

    // Return the currently authenticated user's public details
    public UserResponse getCurrentUser(UserPrincipal principal) {

        return new UserResponse(
                principal.getId(),
                principal.getName(),
                principal.getUsername(),
                principal.getRole()
        );
    }

    // Build JWT response
    private AuthResponse buildAuthResponse(User user) {

        String token = jwtService.generateToken(user);

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationInSeconds(),
                userResponse
        );
    }

    // Normalize email consistently
    private String normalizeEmail(String email) {

        return email.trim().toLowerCase(Locale.ROOT);
    }
}