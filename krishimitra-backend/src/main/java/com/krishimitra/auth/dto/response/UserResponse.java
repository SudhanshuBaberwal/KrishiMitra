package com.krishimitra.auth.dto.response;

import com.krishimitra.auth.model.UserRole;

public record UserResponse(

        String id,
        String name,
        String email,
        UserRole role

) {
}