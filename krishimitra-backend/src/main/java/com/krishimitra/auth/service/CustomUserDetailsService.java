package com.krishimitra.auth.service;

import com.krishimitra.auth.model.User;
import com.krishimitra.auth.repository.UserRepository;
import com.krishimitra.auth.security.UserPrincipal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        String normalizedEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + normalizedEmail
                        )
                );

        return new UserPrincipal(user);
    }
}