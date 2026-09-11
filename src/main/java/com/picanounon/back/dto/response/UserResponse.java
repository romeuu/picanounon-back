package com.picanounon.back.dto.response;

import com.picanounon.back.model.AuthProvider;
import com.picanounon.back.model.Role;
import com.picanounon.back.model.User;

public record UserResponse(
        Long id,
        String email,
        String name,
        String pictureUrl,
        AuthProvider provider,
        Role role
) {
    public static UserResponse from(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPictureUrl(),
                user.getProvider(),
                user.getRole()
        );
    }
}