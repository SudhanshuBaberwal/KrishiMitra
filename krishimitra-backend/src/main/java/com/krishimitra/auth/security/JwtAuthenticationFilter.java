package com.krishimitra.auth.security;

import com.krishimitra.auth.service.CustomUserDetailsService;

import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authorizationHeader =
                request.getHeader("Authorization");

        // Continue if Authorization header is missing
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        final String token = authorizationHeader.substring(7);

        try {

            // Extract email after verifying JWT signature and expiration
            String email = jwtService.extractEmail(token);

            // Authenticate only if no authentication exists already
            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                // Verify token against the current database user
                if (userDetails instanceof UserPrincipal userPrincipal
                        && userPrincipal.isEnabled()
                        && jwtService.isTokenValid(token, userPrincipal)) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userPrincipal,
                                    null,
                                    userPrincipal.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (JwtException | UsernameNotFoundException |
                 IllegalArgumentException exception) {

            // Invalid or expired tokens must not authenticate the request.
            // Continue the chain; Spring Security will reject protected routes.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}