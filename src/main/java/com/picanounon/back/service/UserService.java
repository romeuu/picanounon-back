package com.picanounon.back.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.picanounon.back.dto.auth.RegisterRequest;
import com.picanounon.back.model.AuthProvider;
import com.picanounon.back.model.Role;
import com.picanounon.back.model.User;
import com.picanounon.back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User processOAuthUser(GoogleIdToken.Payload payload) {
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        if (name == null || name.isBlank()) {
            name = email;
        }
        String pictureUrl = (String) payload.get("picture");
        String providerId = payload.getSubject();
        boolean emailVerified = Boolean.TRUE.equals(payload.getEmailVerified());

        Optional<User> existingUserOpt = userRepository.findByEmail(email);

        if (existingUserOpt.isPresent()) {
            User user = existingUserOpt.get();
            user.setName(name);
            if (pictureUrl != null) {
                user.setPictureUrl(pictureUrl);
            }
            if (user.getProviderId() == null) {
                user.setProviderId(providerId);
            }
            if (!user.isEmailVerified() && emailVerified) {
                user.setEmailVerified(true);
            }
            return userRepository.save(user);
        }

        User newUser = User.builder()
                .email(email)
                .name(name)
                .pictureUrl(pictureUrl)
                .provider(AuthProvider.GOOGLE)
                .providerId(providerId)
                .role(Role.ROLE_USER)
                .emailVerified(emailVerified)
                .enabled(true)
                .build();

        return userRepository.save(newUser);
    }

    @Transactional
    public User registerLocal(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already in use: " + req.email());
        }

        User user = User.builder()
                .email(req.email())
                .name(req.name())
                .password(passwordEncoder.encode(req.password()))
                .provider(AuthProvider.LOCAL)
                .role(Role.ROLE_USER)
                .emailVerified(false)
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}