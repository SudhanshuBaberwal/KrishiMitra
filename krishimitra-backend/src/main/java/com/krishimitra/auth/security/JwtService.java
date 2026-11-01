package com.krishimitra.auth.security;

import com.krishimitra.auth.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expiration;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long expiration
    ) {

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
    }

    // Generate JWT token
    public String generateToken(User user) {

        Date issuedAt = new Date();
        Date expirationDate = new Date(
                issuedAt.getTime() + expiration
        );

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .signWith(signingKey)
                .compact();
    }

    // Extract email from token
    public String extractEmail(String token) {

        return extractAllClaims(token).getSubject();
    }

    // Extract user ID from token
    public String extractUserId(String token) {

        return extractAllClaims(token).get("userId", String.class);
    }

    // Validate token against the authenticated user
    public boolean isTokenValid(
            String token,
            UserPrincipal userPrincipal
    ) {

        Claims claims = extractAllClaims(token);

        String email = claims.getSubject();

        return email.equals(userPrincipal.getUsername())
                && claims.getExpiration().after(new Date());
    }

    // Parse and verify JWT signature
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Return configured expiration in seconds
    public long getExpirationInSeconds() {

        return expiration / 1000;
    }
}