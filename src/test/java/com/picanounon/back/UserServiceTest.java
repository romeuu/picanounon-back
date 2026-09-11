package com.picanounon.back;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.picanounon.back.dto.auth.RegisterRequest;
import com.picanounon.back.model.AuthProvider;
import com.picanounon.back.model.Role;
import com.picanounon.back.model.User;
import com.picanounon.back.repository.UserRepository;
import com.picanounon.back.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testProcessOAuthUser_NewUser() {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail("newuser@gmail.com");
        payload.set("name", "New Google User");
        payload.set("picture", "https://photo.url");
        payload.setSubject("google-sub-12345");
        payload.setEmailVerified(true);

        when(userRepository.findByEmail("newuser@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.processOAuthUser(payload);

        assertNotNull(user);
        assertEquals("newuser@gmail.com", user.getEmail());
        assertEquals("New Google User", user.getName());
        assertEquals("https://photo.url", user.getPictureUrl());
        assertEquals("google-sub-12345", user.getProviderId());
        assertEquals(AuthProvider.GOOGLE, user.getProvider());
        assertEquals(Role.ROLE_USER, user.getRole());
        assertTrue(user.isEmailVerified());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testProcessOAuthUser_ExistingUser() {
        User existing = User.builder()
                .id(1L)
                .email("existing@gmail.com")
                .name("Old Name")
                .provider(AuthProvider.GOOGLE)
                .role(Role.ROLE_USER)
                .build();

        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail("existing@gmail.com");
        payload.set("name", "Updated Name");
        payload.set("picture", "https://newphoto.url");
        payload.setSubject("google-sub-999");
        payload.setEmailVerified(true);

        when(userRepository.findByEmail("existing@gmail.com")).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.processOAuthUser(payload);

        assertEquals("Updated Name", user.getName());
        assertEquals("https://newphoto.url", user.getPictureUrl());
        assertEquals("google-sub-999", user.getProviderId());
        assertTrue(user.isEmailVerified());
    }

    @Test
    void testRegisterLocal_Success() {
        RegisterRequest req = new RegisterRequest("local@picanounon.com", "secret123", "Local User");

        when(userRepository.existsByEmail(req.email())).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.registerLocal(req);

        assertNotNull(user);
        assertEquals("local@picanounon.com", user.getEmail());
        assertEquals("hashedPassword", user.getPassword());
        assertEquals(AuthProvider.LOCAL, user.getProvider());
    }

    @Test
    void testRegisterLocal_DuplicateEmailThrowsException() {
        RegisterRequest req = new RegisterRequest("duplicate@picanounon.com", "secret123", "User");

        when(userRepository.existsByEmail(req.email())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.registerLocal(req));
        verify(userRepository, never()).save(any(User.class));
    }
}