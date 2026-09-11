package com.picanounon.back.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record GoogleTokenRequest(
        @NotBlank(message = "idToken is required")
        String idToken
) {}