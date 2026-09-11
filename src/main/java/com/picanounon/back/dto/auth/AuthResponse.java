package com.picanounon.back.dto.auth;

import com.picanounon.back.dto.response.UserResponse;

public record AuthResponse(
        String accessToken,
        String tokenType,
        UserResponse user
) {
    public AuthResponse(String accessToken, UserResponse user) {
        this(accessToken, "Bearer", user);
    }
}