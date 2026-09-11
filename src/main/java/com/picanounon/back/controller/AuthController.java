package com.picanounon.back.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.picanounon.back.dto.auth.AuthResponse;
import com.picanounon.back.dto.auth.GoogleTokenRequest;
import com.picanounon.back.dto.auth.LoginRequest;
import com.picanounon.back.dto.auth.RegisterRequest;
import com.picanounon.back.dto.response.ApiResponse;
import com.picanounon.back.dto.response.UserResponse;
import com.picanounon.back.model.User;
import com.picanounon.back.security.jwt.JwtTokenProvider;
import com.picanounon.back.service.GoogleAuthService;
import com.picanounon.back.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GoogleAuthService googleAuthService;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authManager;

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> loginWithGoogle(@Valid @RequestBody GoogleTokenRequest req) {
        GoogleIdToken.Payload payload = googleAuthService.verifyToken(req.idToken());
        User user = userService.processOAuthUser(payload);
        String appToken = tokenProvider.createToken(user);
        return ResponseEntity.ok(ApiResponse.success(new AuthResponse(appToken, UserResponse.from(user))));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginLocal(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );
        User user = (User) auth.getPrincipal();
        String appToken = tokenProvider.createToken(user);
        return ResponseEntity.ok(ApiResponse.success(new AuthResponse(appToken, UserResponse.from(user))));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
        User user = userService.registerLocal(req);
        String appToken = tokenProvider.createToken(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", new AuthResponse(appToken, UserResponse.from(user))));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated"));
        }
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(user)));
    }
}