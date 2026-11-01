package com.krishimitra.auth.security;

import com.krishimitra.auth.model.User;
import com.krishimitra.auth.model.UserRole;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    @Getter
    private final String id;
    @Getter
    private final String name;
    private final String email;
    private final String password;
    @Getter
    private final UserRole role;
    private final boolean enabled;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.enabled = user.isEnabled();
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public @NonNull String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}